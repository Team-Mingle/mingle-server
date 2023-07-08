package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.TokenHelper;
import community.mingle.app.src.auth.model.*;
import community.mingle.app.src.domain.*;
import community.mingle.app.utils.JwtService;
import community.mingle.app.utils.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

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
//    private final TokenService tokenService;

    private final TokenHelper accessTokenHelper;
    private final TokenHelper refreshTokenHelper;

    private final SpringTemplateEngine springTemplateEngine;


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
    public void verifyEmail(PostUserEmailRequest postUserEmailRequest) throws BaseException {
        try {
            String email = new SHA256().encrypt(postUserEmailRequest.getEmail());
            postUserEmailRequest.setEmail(email);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }
        if ((authRepository.findEmail(postUserEmailRequest.getEmail()) == true)) {
            if (authRepository.findMember(postUserEmailRequest.getEmail()).getStatus().equals(UserStatus.INACTIVE)) {
                throw new BaseException(USER_DELETED_ERROR);
            } else {
            throw new BaseException(USER_EXISTS_EMAIL);
            }
        }
    }


    /**
     * 1.4.1 인증번호 생성
     */
    @Transactional
    public void sendCode(String email) throws BaseException {
        try {
            Random random = new Random();
            String authKey = String.valueOf(random.nextInt(888888) + 111111);
            sendAuthEmail(email, authKey); /**/
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException(CODE_GENERATE_FAIL);
        }
    }

    /**
     * 1.4.2 인증번호 이메일 전송
     */
    private void sendAuthEmail(String email, String authKey) throws BaseException {
        String subject = "Mingle의 이메일 인증번호를 확인하세요";
        String text = "\n\n인증번호는 " + authKey + " 입니다.";
        Context context = new Context();
        context.setVariable("authKey", authKey);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject(subject);
            String html = springTemplateEngine.process("index", context);
            helper.setText(html, true);
            helper.addInline("image", new ClassPathResource("templates/images/image-1.jpeg"));
            javaMailSender.send(mimeMessage);

        } catch(MessagingException e) {
            e.printStackTrace();
            throw new BaseException(EMAIL_SEND_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

        try {
            redisUtil.setDataExpire(email, authKey, 60 * 3L);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 1.5 인증 코드 검사 API
     */
    public void authCode(String email, String code) throws BaseException {
        String domain = email.split("@")[1];
        if (domain.equals("freshman.mingle.com")) {
            if (!(code.equals(redisUtil.getData(email)))) {
                throw new BaseException(EMAIL_CODE_FAIL);
            }
            redisUtil.deleteData(email);
            return;
        }

        if (redisUtil.getData(email) == null) {
            throw new BaseException(EMAIL_CODE_EXPIRED);
        }
        if (!(code.equals(redisUtil.getData(email)))) {
            throw new BaseException(EMAIL_CODE_FAIL);
        }
        if (code.equals(redisUtil.getData(email))) {
            return;
        }
    }

    /**
     * 1.8 회원가입 api
     */
    @Transactional //Transaction silently rolled back because it has been marked as rollback-only
    public PostSignupResponse createMember(PostSignupRequest postSignupRequest) throws BaseException {
        String domain = postSignupRequest.getEmail().split("@")[1];
        String freshmanEmail = postSignupRequest.getEmail().split("@")[0];
        if (postSignupRequest.getUnivId() == 6 && domain.equals("freshman.mingle.com")) {
            String univName = freshmanEmail.split("\\.")[0];
            switch (univName) {
                case "hku":
                    postSignupRequest.setUnivId(1);
                    break;
                case "hkust":
                    postSignupRequest.setUnivId(2);
                    break;
                case "cuhk":
                    postSignupRequest.setUnivId(3);
                    break;
                case "cityu":
                    postSignupRequest.setUnivId(4);
                    break;
                case "polyu":
                    postSignupRequest.setUnivId(5);
                    break;
                default:
                    throw new BaseException(INVALID_UNIV_ID);
            }
        }

        //닉네임 중복검사 먼저
        if (authRepository.findNickname(postSignupRequest.getNickname()) == true) {
            throw new BaseException(USER_EXISTS_NICKNAME);
        }
        //없는 univId 일때 추가
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
        if ((authRepository.findEmail(email) == true)) {  //탈퇴 유저 재가입 방지
            if (authRepository.findMember(email).getStatus().equals(UserStatus.INACTIVE)) {
                throw new BaseException(USER_DELETED_ERROR);
            } else {
            throw new BaseException(USER_EXISTS_EMAIL);
            }
        }

        //로직
        try {
            UnivName univName = authRepository.findUniv(postSignupRequest.getUnivId());
            if (domain.equals("freshman.mingle.com")) {
                Member member = Member.createFreshman(univName, postSignupRequest.getNickname(), postSignupRequest.getEmail(), postSignupRequest.getPwd());
                Long id = authRepository.save(member);
                return new PostSignupResponse(id);
            }
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

        Member member = authRepository.findMemberByEmail(postLoginRequest.getEmail()); //이메일로 유저 찾기
        if (member == null) { //없는 유저
            throw new BaseException(FAILED_TO_LOGIN);
        } //비밀번호 불일치
        if (!(member.getPwd().equals(encryptPwd))) { //Member 에게 받아온 비밀번호와 방금 암호화한 비밀번호를 비교
            throw new BaseException(FAILED_TO_LOGIN);
        }
        //탈퇴 유저 재로그인 방지
//        if (!(Objects.isNull(member.getDeletedAt())) && (member.getRole().equals(UserStatus.INACTIVE))) {
//        if (member.getRole().equals(UserStatus.INACTIVE.toString())) {
        if (member.getStatus().equals(UserStatus.INACTIVE)) {
            throw new BaseException(USER_DELETED_ERROR);
        } //신고 유저 로그인 방지
//        if (member.getRole().equals(UserStatus.REPORTED.name())) {
        if (member.getStatus().equals(UserStatus.REPORTED)) {
            throw new BaseException(USER_REPORTED_ERROR);
        }
        try { //토큰 발급
            Long memberId = member.getId();
            UserRole memberRole = member.getRole();
            TokenHelper.PrivateClaims privateClaims = createPrivateClaims(memberId, memberRole);
            String accessToken = accessTokenHelper.createAccessToken(privateClaims);
            String refreshToken = refreshTokenHelper.createRefreshToken(privateClaims, postLoginRequest.getEmail());
            member.setFcmToken(postLoginRequest.getFcmToken());
            return new PostLoginResponse(memberId, postLoginRequest.getEmail(), member.getNickname(),member.getUniv().getUnivName().substring(0,3) ,accessToken, refreshToken); //비교해서 이상이 없다면 jwt를 발급
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
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

    /**
     * 1.11 비밀번호 재설정 용 인증번호 보내기 API
     * 등록된 이메일인지 확인 후 sendAuthEmail
     */
    @Transactional
    public void sendCodeForPwd (PostEmailRequest req) throws BaseException {
        String encryptedEmail;
        try {
             encryptedEmail = new SHA256().encrypt(req.getEmail());
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }

        if ((authRepository.findEmail(encryptedEmail) == false)) {
            throw new BaseException(USER_NOT_EXIST);
        }

        try {
            Random random = new Random();
            String authKey = String.valueOf(random.nextInt(888888) + 111111);
            sendAuthEmail(req.getEmail(), authKey); // 1.4.2 authService
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(EMAIL_SEND_FAIL);
        }
    }


    /**
     * 1.12 refresh token으로 access Token 발급
     */
    public ReissueAccessTokenDTO reissueAccessToken(String rToken, String email) throws BaseException{

        //orElseThrow 알아보기
        TokenHelper.PrivateClaims privateClaims = refreshTokenHelper.parseRefreshToken(rToken, email).orElseThrow();
        String accessToken = accessTokenHelper.createAccessToken(privateClaims);
        //refreshToken 으로 재발급
        String refreshToken = refreshTokenHelper.createRefreshToken(privateClaims, email);
        return new ReissueAccessTokenDTO(accessToken, refreshToken);
    }



    /**
     * PrivateClaim 발급
     */
    public TokenHelper.PrivateClaims createPrivateClaims(Long memberId, UserRole memberRole) {
        return new TokenHelper.PrivateClaims(String.valueOf(memberId), memberRole);
    }

    /**
     * 1.13 fcm token refresh api
     */
    @Transactional
    public void refreshFcmToken(FcmTokenRequest fcmTokenRequest) throws BaseException {
        Long userIdx = jwtService.getUserIdx();
        try {
            Member member = authRepository.findMemberById(userIdx);
            member.setFcmToken(fcmTokenRequest.getFcmToken());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String generateFreshmanEmail(String univName) throws BaseException {
        while (true) {
        String freshmanEmail = univName + "." + generateRandomCode(3) + "@freshman.mingle.com";
        boolean isDuplicate = validateRandomEmail(freshmanEmail);
            if (isDuplicate == false) {
                return freshmanEmail;
            }
        }
    }

    public boolean validateRandomEmail(String freshmanEmail) throws BaseException {
        String encryptedEmail;
        try {
            encryptedEmail = new SHA256().encrypt(freshmanEmail);
        } catch (Exception ignored) {
            throw new BaseException(EMAIL_ENCRYPTION_ERROR);
        }
        return authRepository.findEmail(encryptedEmail);
    }

    private static String generateRandomCode(int length) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random random = new Random(System.currentTimeMillis());
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }


}
