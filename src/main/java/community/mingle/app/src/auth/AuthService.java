package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.auth.authModel.PostEmailRequest;
import community.mingle.app.src.auth.authModel.PostPwdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Random;

import static community.mingle.app.config.BaseResponseStatus.*;

@Component
@RequiredArgsConstructor
public class AuthService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private  String from;

    /**
     * 1.4.1 인증번호 생성
     */
    @Transactional
    public void sendCode(PostEmailRequest request) throws BaseException {


//        if (emailDao.getEmail(request.getEmail().equals(request.getEmail()))) {
//            throw new BaseException()
//        }

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

