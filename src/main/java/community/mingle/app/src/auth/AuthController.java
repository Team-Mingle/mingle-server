package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.authModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.utils.ValidationRegex.isRegexEmail;
import static community.mingle.app.utils.ValidationRegex.isRegexPassword;

@RestController
@RequestMapping("/emails")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 1.4.1 인증코드 전송 API
     * @return
     */
    @PostMapping("")
    public BaseResponse<String> sendCode(@RequestBody @Valid PostEmailRequest req) {
        try {
            if(!isRegexEmail(req.getEmail())){ //이메일 형식(정규식) 검증
                return new BaseResponse<>(EMAIL_FORMAT_ERROR);
            }
            authService.sendCode(req);
            //return ResponseEntity.ok().build();
            String result = "인증번호가 전송되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 1.4.2 인증 코드 검사 API
     */
    @ResponseBody
    @PostMapping("code")
    public BaseResponse<String> verifyCode(@RequestBody @Valid PostCodeRequest code) {
        try {
            if(!isRegexEmail(code.getEmail())){ //이메일 형식(정규식) 검증
                return new BaseResponse<>(EMAIL_FORMAT_ERROR);
            }
            authService.authCode(code.getCode(), code.getEmail());
//            CodeResponse codeRes = emailService.authCode(code.getCode(), code.getEmail());
            String result = "인증에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.5 비밀번호 검증 api
     */
    @ResponseBody
    @PostMapping("pwd") //Get 인데 Body 로 받을수 있나?
    public BaseResponse<String> verifyPwd(@RequestBody PostPwdRequest postPwdRequest) {
        try {
            if (postPwdRequest.getPwd().length() == 0) {
                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
            }
            if (postPwdRequest.getPwd().length() < 8) {
                return new BaseResponse<>(PASSWORD_LENGTH_ERROR);
            }
            if (!isRegexPassword(postPwdRequest.getPwd())) {
                return new BaseResponse<>(PASSWORD_FORMAT_ERROR);
            }
            authService.verifyPwd(postPwdRequest);
            String result = "비밀번호 인증에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.8 회원가입 api
     */
    @ResponseBody
    @PostMapping("signup")
    public BaseResponse<PostSignupResponse>createUser(@RequestBody PostSignupRequest postSignupRequest){
        try {

            authService.verifyNickname(postSignupRequest);

            if (!isRegexEmail(postSignupRequest.getEmail())) { //이메일 형식(정규식) 검증
                return new BaseResponse<>(EMAIL_FORMAT_ERROR);
            }
            if (postSignupRequest.getPwd().length() == 0) {
                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
            }
            if (postSignupRequest.getPwd().length() < 8) {
                return new BaseResponse<>(PASSWORD_LENGTH_ERROR);
            }
            if (!isRegexPassword(postSignupRequest.getPwd())) {
                return new BaseResponse<>(PASSWORD_FORMAT_ERROR);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


    }


}
