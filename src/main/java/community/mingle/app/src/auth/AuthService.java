package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.auth.model.*;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.UnivEmail;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static community.mingle.app.config.BaseResponseStatus.*;

//@Component
@RequiredArgsConstructor
@Service
//@Transactional(readOnly = true)
public class AuthService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final AuthRepository authRepository;
    private final JwtService jwtService;

    @Value("${spring.mail.username}")
    private  String from;

    /**
     * 1.1 학교 리스트 전송 API
     */
    public List<UnivName> findUniv() throws BaseException{
        try{
            List<UnivName> univName = authRepository.findAll();
            return univName;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    /**
     * 1.2 학교별 도메인 리스트 전송 API
     */
    public List<UnivEmail> findDomain(int univId) throws BaseException {
        try {
            List<UnivEmail> getDomain = authRepository.findDomainByUnivId(univId);
            return getDomain;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 1.3 이메일 입력 & 중복검사 API
     */
    @Transactional
    public String verifyEmail(PostUserEmailRequest postUserEmailRequest) throws BaseException {

        try {
            String email = new SHA256().encrypt(postUserEmailRequest.getEmail());
            postUserEmailRequest.setEmail(email);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }

        try {
            if ((authRepository.findEmail(postUserEmailRequest.getEmail()) == true)) {
                throw new BaseException(USER_EXISTS_EMAIL);
            }
        } catch (Exception e) {
            throw new BaseException(USER_EXISTS_EMAIL);
        }

        return null;
    }


    /**
     * 1.4.1 인증번호 생성
     */
    @Transactional
    public void sendCode(PostEmailRequest request) throws BaseException {
        try {
            Random random = new Random();
            String authKey = String.valueOf(random.nextInt(888888) + 111111);
            sendAuthEmail(request.getEmail(), authKey); /**/
        } catch (BaseException e) {
            throw new BaseException(CODE_GENERATE_FAIL);
        }
    }

    /**
     * 1.4.1 인증번호 이메일 전송
     */
    private void sendAuthEmail(String email, String authKey) throws BaseException {
        String subject = "Mingle의 이메일을 인증하세요!";
        String text = "\n\n인증번호는" + authKey + "입니다.";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(mimeMessage);

        } catch(MessagingException e) {
//            e.printStackTrace();
            throw new BaseException(EMAIL_SEND_FAIL);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        try {
            redisUtil.setDataExpire(authKey, email, 60 * 5L);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 1.5 인증 코드 검사 API
     */
    public void authCode(String email, String code) throws BaseException {

//        if (redisUtil.getData(email) == null) {
//            throw new BaseException(TIME_EXPIRED ERROR);
//        }


        try {
            if (code.equals(redisUtil.getData(email))) {
                return;
//            return new CodeResponse("인증에 성공하였습니다.");
            } else {
                throw new BaseException(EMAIL_CODE_FAIL);
            }
        } catch (Exception e) {
            throw new BaseException(EMAIL_CODE_FAIL);
        }

    }

    /**
     * 1.8 회원가입 api
     */
    @Transactional //Transaction silently rolled back because it has been marked as rollback-only
    public PostSignupResponse createMember(PostSignupRequest postSignupRequest) throws BaseException {

        //닉네임 중복검사 먼저

        if (authRepository.findNickname(postSignupRequest.getNickname()) == true) {
            throw new BaseException(USER_EXISTS_NICKNAME);
        }

//        없는 univId 일때 추가
        if (authRepository.findUniv(postSignupRequest.getUnivId()) == null) {
            throw new BaseException(INVALID_UNIV_ID);
        }

        //이메일 암호화
        String email;
        try {
            email = new SHA256().encrypt(postSignupRequest.getEmail());
            postSignupRequest.setEmail(email);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }

        //비밀번호 암호화
        String pwd;
        try {
            pwd = new SHA256().encrypt(postSignupRequest.getPwd());
            postSignupRequest.setPwd(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        //이메일 중복검사
        /** 얘를 try catch 밖으로 빼니 콘솔에 에러 문구가 안뜸. (??) */
        if ((authRepository.findEmail(email) == true)) {
            throw new BaseException(USER_EXISTS_EMAIL);
        }

        //로직
        try {
            UnivName univName = authRepository.findUniv(postSignupRequest.getUnivId());
            Member member = Member.createMember(univName, postSignupRequest.getNickname(), postSignupRequest.getEmail(), postSignupRequest.getPwd());

            Long id = authRepository.save(member);
            return new PostSignupResponse(id);

        } catch (Exception e) {
            throw new BaseException(FAILED_TO_SIGNUP);
        }

    }

    /**
     * 1.9 로그인 api
     */
    @Transactional
    public PostLoginResponse logIn (PostLoginRequest postLoginRequest) throws BaseException {
        //이메일 암호화
        String email;
        try {
            email = new SHA256().encrypt(postLoginRequest.getEmail());
            postLoginRequest.setEmail(email);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }

        //비밀번호 암호화
        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(postLoginRequest.getPwd());
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        Member member;

        member = authRepository.findMemberByEmail(postLoginRequest.getEmail());
        if (member == null) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        //JWT 로 찾은 user_id 랑 email 로 찾은 user_id 랑 같은지 검증 (할필요 없음, 로그인은 header 안 씀)
//        Long userIdxByJwt = Service.getUserIdx();
//        if (member.getId() != userIdxByJwt) {
//            throw new BaseException(INVALID_USER_JWT);
//        }

        if (!(member.getPwd().equals(encryptPwd))) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        try {
            Long userIdx = member.getId(); //Member 에게 받아온 비밀번호와 방금 암호화한 비밀번호를 비교
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginResponse(userIdx, jwt); //비교해서 이상이 없다면 jwt를 발급
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_CREATEJWT);
        }
    }

    /**
     * 1.10 비밀번호 재설정 api
     */
    @Transactional
    public void updatePwd (PatchUpdatePwdRequest patchUpdatePwdRequest) throws BaseException {
        //이메일 암호화
        String email;
        try {
            email = new SHA256().encrypt(patchUpdatePwdRequest.getEmail());
            patchUpdatePwdRequest.setEmail(email);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }

//        비밀번호 암호화
        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(patchUpdatePwdRequest.getPwd());
            patchUpdatePwdRequest.setPwd(encryptPwd);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        //이메일로 멤버 찾기
        Member member = authRepository.findMemberByEmail(patchUpdatePwdRequest.getEmail());
        if (member == null) {
            throw new BaseException(USER_NOT_EXIST);
        }

        try {
            member.setPwd(patchUpdatePwdRequest.getPwd()); //Setter 로 바로 해도 될까?
            Long id = authRepository.save(member);
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_CHANGEPWD);
        }

    }
}