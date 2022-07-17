package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.auth.authModel.*;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.UnivEmail;
import community.mingle.app.src.domain.UnivName;
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


    @Value("${spring.mail.username}")
    private  String from;


    /**
     * 학교 리스트 보내주기
     *
     * @return
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
     * 학교 univIdx 받고 이메일 리스트 보내주기
     */
    public List<UnivEmail> findDomain(int univId) throws BaseException {
        try {
            List<UnivEmail> getDomain = authRepository.findByUniv(univId);
            return getDomain;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 이메일 받기
     */
    @Transactional
    public PostUserEmailResponse verifyEmail(PostUserEmailRequest postUserEmailRequest) throws BaseException {

        if ((authRepository.findEmail(postUserEmailRequest.getEmail()) == true)) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        try {
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
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
            throw new BaseException(e.getStatus());
        }
    }

    /**
     * 1.4.1 인증번호 아매알전송
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

//        } catch (BaseException e) {
//            throw new BaseException(e.getStatus());
        } catch(MessagingException e) {
            e.printStackTrace();
//            throw new BaseException(e);
        }
        redisUtil.setDataExpire(authKey, email, 60*5L);
    }

    /**
     * 1.4.2 인증 코드 검사 API
     */
    public void authCode(String email, String code) throws BaseException {
        if (code.equals(redisUtil.getData(email))) {
            return;
//            return new CodeResponse("인증에 성공하였습니다.");
        } else {
            throw new BaseException(EMAIL_CODE_FAIL);
        }
    }

    /**
     * 1.5 비밀번호 검사
     */
    public void verifyPwd(PostPwdRequest postPwdRequest) throws BaseException {
        if ((postPwdRequest.getPwd().compareTo(postPwdRequest.getRePwd())) == 0) {
            return;
        } else if ((postPwdRequest.getPwd().compareTo(postPwdRequest.getRePwd())) != 0) {
            throw new BaseException(PASSWORD_MATCH_ERROR);
        } else {
            throw new BaseException(SERVER_ERROR); //DB 접근을 안하니 디비에러는 아니고 그냥 서버에러로?
        }
    }


    /**
     * 1.8 회원가입 api
     * 암호화, jwt 보류
     */
    @Transactional //Transaction silently rolled back because it has been marked as rollback-only
    public PostSignupResponse createMember(PostSignupRequest postSignupRequest) throws BaseException {

        //중복검사
        if ((authRepository.findEmail(postSignupRequest.getEmail()) == true)) {
            //암호화 전 or 후 ?
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (authRepository.findNickname(postSignupRequest.getNickname()) == true) {
            throw new BaseException(POSTS_USERS_EXISTS_NICKNAME);
        }

//        String EncryptedEmail = postSignupRequest.getEmail();
//        String pwd = postSignupRequest.getPwd();

        try {
            //암호화하기
            UnivName univName = authRepository.findUniv(postSignupRequest.getUnivId());
            Member member = Member.createMember(univName, postSignupRequest.getNickname(), postSignupRequest.getEmail(), postSignupRequest.getPwd());
            System.out.println("====1. createMember====="); //실행됨

            Long id = authRepository.save(member);
            System.out.println("====2. save====="); //실행안됨
//            authRepository.save(member);
            return new PostSignupResponse(id);

//            return new PostSignupResponse(jwt, memberId);

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 1.9 로그인 api
     */

    public PostLoginResponse logIn (PostLoginRequest postLoginRequest) throws BaseException {

        if((authRepository.findEmail(postLoginRequest.getEmail())==false)){
            throw new BaseException(FAILED_TO_LOGIN);
        }

        Member member = authRepository.findMember(postLoginRequest.getEmail());
        if (!member.getPwd().equals(postLoginRequest.getPwd())) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
        return new PostLoginResponse(member.getEmail());

       /*
        try {
            Member member = authRepository.findMember(postLoginRequest.getEmail());
            //to be added
            //String nickname = member.getNickname();
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        } */

    }

    /**
     * 1.10 비밀번호 재설정 api
     */
    @Transactional
    public void updatePwd (PatchUpdatePwdRequest patchUpdatePwdRequest) throws BaseException{
        //여기서 JWT로 해당 유저인지 확인 필요
        //임시 방편으로 중복확인 메소드 넣어둠
        if ((authRepository.findEmail(patchUpdatePwdRequest.getEmail()) == false)) {
            //암호화 전 or 후 ?
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if ((patchUpdatePwdRequest.getPwd().compareTo(patchUpdatePwdRequest.getRePwd())) == 0) {
            Member member = authRepository.findMember(patchUpdatePwdRequest.getEmail());
            member.setPwd(patchUpdatePwdRequest.getPwd());
            Long id = authRepository.save(member);
        } else if ((patchUpdatePwdRequest.getPwd().compareTo(patchUpdatePwdRequest.getRePwd())) != 0) {
            throw new BaseException(PASSWORD_MATCH_ERROR);
        }


    }

//    /**
//     * 1.5 비밀번호 검사
//     */
//    public void verifyPwd(PwdRequest pwdRequest) throws BaseException {
//        try {
//            if ((pwdRequest.getPwd().compareTo(pwdRequest.getRePwd())) != 0) {
//                System.out.println("match error");
//                throw new BaseException(PASSWORD_MATCH_ERROR);
//            }
//        } catch (Exception e) { //Base 말고 더 제너럴하게 잡아주는듯 하다.
//            System.out.println("general ex");
//            throw new BaseException(SERVER_ERROR); //DB 접근을 안하니 디비에러는 아니고 그냥 서버에러로?
//        }
//    }
}

