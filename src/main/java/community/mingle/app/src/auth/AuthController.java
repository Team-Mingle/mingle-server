package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.authModel.*;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.UnivName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.*;
import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.utils.ValidationRegex.isRegexEmail;
import static community.mingle.app.utils.ValidationRegex.isRegexPassword;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class AuthController {
//    @Autowired
    private final AuthService authService;

    /**
     * 1.4.1 인증코드 전송 API
     * @return
     */
    @PostMapping("")
    public BaseResponse<String> sendCode(@RequestBody @Valid PostEmailRequest req) {
        try {
            if (!isRegexEmail(req.getEmail())) { //이메일 형식(정규식) 검증
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
     * 1.6.1 Alternative 약관만 스트링으로 반환
     */
    @GetMapping("terms/1")
    public String getTermsString() {

        try {
            String filePath = "src/main/java/community/mingle/app/config/personalinfoterms";
            FileInputStream fileStream = null;

            fileStream = new FileInputStream(filePath);
            byte[] readBuffer = new byte[fileStream.available()];
            while (fileStream.read( readBuffer ) != -1){}
            fileStream.close();
            return (new String(readBuffer));

        } catch (IOException e) {
            return "약관을 불러오는데 실패하였습니다.";
        }
    }


    /**
     * 1.6 서비스이용약관
     * @return
     */
    @GetMapping("terms")
    public String getServiceTerms() {
        try {
            String filePath = "src/main/java/community/mingle/app/config/serviceUsageTerms";
            FileInputStream fileStream = null;

            fileStream = new FileInputStream(filePath);
            byte[] readBuffer = new byte[fileStream.available()];
            while (fileStream.read( readBuffer ) != -1){}
            fileStream.close();
            return (new String(readBuffer));

        } catch (IOException e) {
            return "약관을 불러오는데 실패하였습니다.";
        }
    }

    /**
     * 1.6.2 개인정보 처리방침
     * isSucceess, code, message, result 가 \n 과 같이 나옴
     */
    @GetMapping("terms/2")
    public BaseResponse<String> getPersonalTerms() {

        try {
            String filePath = "src/main/java/community/mingle/app/config/personalinfoterms";
            FileInputStream fileStream = null;

            fileStream = new FileInputStream(filePath);
            byte[] readBuffer = new byte[fileStream.available()];
            while (fileStream.read(readBuffer) != -1) {
            }
            fileStream.close(); //스트림 닫기
            return new BaseResponse<>(new String(readBuffer));
        } catch (IOException e) {
            return new BaseResponse<>("약관을 불러오는데 실패하였습니다.");
        }

        /**
         * 통으로나옴
         */
//        File file = new File("src/main/java/community/mingle/app/config/personalinfoterms");
//        StringBuilder sb = new StringBuilder();
//        Scanner scan = new Scanner(file);
//        while(scan.hasNextLine()){
//            sb.append(scan.nextLine());
//        }
//        String result = sb.toString();
//        return new BaseResponse<>(result);


        /**
         * \n 나오는방법
         */
//        String str = Files.readString(Paths.get("src/main/java/community/mingle/app/config/personalinfoterms"));
//        return new BaseResponse<>(str);
    }

    /**
     * 1.8 회원가입 api
     */
    @ResponseBody
    @PostMapping("signup")
    public BaseResponse<PostSignupResponse> createMember (@RequestBody @Valid PostSignupRequest postSignupRequest){
        //이메일 빔
        if (postSignupRequest.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 형식(정규식) 검증 (new)
        if (!isRegexEmail(postSignupRequest.getEmail())) {
            return new BaseResponse<>(EMAIL_FORMAT_ERROR);
        }
        // 비밀번호 빔
        if (postSignupRequest.getPwd().length() == 0) {
            return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
        }
        //비밀번호 길이
        if (postSignupRequest.getPwd().length() < 8) {
            return new BaseResponse<>(PASSWORD_LENGTH_ERROR);
        }
        //비밀번호 정규표현
        if (!isRegexPassword(postSignupRequest.getPwd())) {
            return new BaseResponse<>(PASSWORD_FORMAT_ERROR);
        }

        try {
            PostSignupResponse postSignupResponse = authService.createMember(postSignupRequest);
            return new BaseResponse<>(postSignupResponse);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 1.9 로그인 api
     */
    @PostMapping("login")
    public BaseResponse<PostLoginResponse> logIn (@RequestBody @Valid PostLoginRequest postLoginRequest) {
        try {
            if (!isRegexEmail(postLoginRequest.getEmail())) { //이메일 정규표현
                return new BaseResponse<>(EMAIL_FORMAT_ERROR);
            }

            if (!isRegexPassword(postLoginRequest.getPwd())) { //비밀번호 정규표현
                return new BaseResponse<>(PASSWORD_FORMAT_ERROR);
            }


            //return ResponseEntity.ok().build();

            PostLoginResponse postloginResponse = authService.logIn(postLoginRequest);
            return new BaseResponse<>(postloginResponse);


        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 1.10 비밀번호 변경 api
     */
    @PatchMapping("userinfo")
    public BaseResponse<String> updatePwd(@RequestBody @Valid UpdatePwdRequest updatePwdRequest) {
        try {
            authService.updatePwd(updatePwdRequest);
            String result = "비밀번호 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}


