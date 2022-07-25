package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.model.*;
import community.mingle.app.src.domain.UnivEmail;
import community.mingle.app.src.domain.UnivName;
//<<<<<<< HEAD
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//=======
import community.mingle.app.utils.JwtService;
//import io.swagger.annotations.Api;
//>>>>>>> 2d2c252c23b3820543db375698b79b1fccd7751e
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.utils.ValidationRegex.isRegexEmail;
import static community.mingle.app.utils.ValidationRegex.isRegexPassword;
//memo
//<<<<<<< HEAD
//@Tag(name = "auth", description = "회원가입 process 관련 API")
//=======
////@Api(tags = {"API 정보를 제공하는 Controller"})
//>>>>>>> 2d2c252c23b3820543db375698b79b1fccd7751e
@CrossOrigin(origins = "")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    //    @Autowired
    private final AuthService authService;
    private final JwtService jwtService;


    /**
     * 1.1 학교 리스트 전송 API
     */

    //@Operation(summary = "1.1 get univ list API", description = "1.1 대학교 리스트 가져오기")

    @GetMapping("/univList")
    public BaseResponse<List<GetUnivListResponse>> univName() {
        try {
            List<UnivName> findUnivNames = authService.findUniv();
            List<GetUnivListResponse> result = findUnivNames.stream()
                    .map(m -> new GetUnivListResponse(m))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 1.2 학교별 도메인 리스트 전송 API
     */

    //@Operation(summary = "1.2 get email domain list by univ API", description = "1.2 대학교 별 이메일 도메인 리스트 가져오기")

    @ResponseBody
    @GetMapping("/univDomain")
    public BaseResponse<List<GetUnivDomainResponse>> getDomain(@RequestParam int univId) {
        try{

            List<UnivEmail> findUnivDomains = authService.findDomain(univId);
            List<GetUnivDomainResponse> result = findUnivDomains.stream()
                    .map(m -> new GetUnivDomainResponse(m.getId(), m.getDomain()))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 1.3 이메일 입력 & 중복검사 API
     */

    //@Operation(summary = "1.3 email duplicate check API", description = "1.3 이메일 입력 & 중복검사 API")
//    @Parameter(name = "email", description = "회원가입 때 사용하는 이메일", example = "example@mingle.com")
    @ResponseBody
    @PostMapping("checkEmail") // (POST) 127.0.0.1:9000/users
    public BaseResponse<String> verifyEmail(@RequestBody PostUserEmailRequest postUserEmailRequest) {

        if (postUserEmailRequest.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if (!isRegexEmail(postUserEmailRequest.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            String result = authService.verifyEmail(postUserEmailRequest);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 1.4 인증코드 전송 API
     * @return
     */
    //@Operation(summary = "1.4 email verification code send API", description = "1.4 이메일 인증코드 전송 API")
//    @Parameter(name = "email", description = "회원가입 때 사용하는 이메일", example = "example@mingle.com")

    @PostMapping("sendCode")
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
     * 1.5 인증 코드 검사 API
     */
    //프론트 실수로 이메일 잘못 받았을 때 validation
    //@Operation(summary = "1.5 email verification code check API", description = "1.5 이메일 인증코드 검사 API")
//    @Parameters({
//            @Parameter(name = "email", description = "인증코드가 전송된 이메일", example = "example@mingle.com"),
//            @Parameter(name = "code", description = "이메일로 발송된 인증코드", example = "495032")
//    })

    @ResponseBody
    @PostMapping("checkCode")
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

//    /**
//     * 1.5 비밀번호 검증 API
//     */
//    @ResponseBody
//    @PostMapping("pwd") //Get 인데 Body 로 받을수 있나?
//    public BaseResponse<String> verifyPwd(@RequestBody PostPwdRequest postPwdRequest) {
//        try {
//            if (postPwdRequest.getPwd().length() == 0) {
//                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
//            }
//            if (postPwdRequest.getPwd().length() < 8) {
//                return new BaseResponse<>(PASSWORD_LENGTH_ERROR);
//            }
//            if (!isRegexPassword(postPwdRequest.getPwd())) {
//                return new BaseResponse<>(PASSWORD_FORMAT_ERROR);
//            }
//            authService.verifyPwd(postPwdRequest);
//            String result = "비밀번호 인증에 성공하였습니다.";
//            return new BaseResponse<>(result);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }


    /**
     * 1.6.1 개인정보 처리방침- Alternative 스트링으로 반환
     */
    //@Operation(summary = "1.6.1 get privacy policy API v1", description = "1.6.1 개인정보처리방침 가져오기 API v1")

    @GetMapping("terms/privacy/1")
    public String getPrivacyTerms1() {
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
     * 1.6.2 개인정보 처리방침
     * isSucceess, code, message, result 가 \n 과 같이 나옴
     */
    //@Operation(summary = "1.6.2 get privacy policy API v2", description = "1.6.2 개인정보처리방침 가져오기 API v2")

    @GetMapping("terms/privacy/2")
    public BaseResponse<String> getPrivacyTerms2() {
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
     * 1.7 서비스이용약관
     */
    //@Operation(summary = "1.7 get terms of policy API", description = "1.7 서비스이용약관 가져오기 API")

    @GetMapping("terms/service")
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
     * 1.8 회원가입 API + JWT
     */
    //@Operation(summary = "1.8 sign up API", description = "1.8 회원가입 API")

//    @Parameters({
//            @Parameter(name = "univId", description = "대학교 식별자", example = "1"),
//            @Parameter(name = "email", description = "이메일 인증 떄 사용한 이메일", example = "example@mingle.com"),
//            @Parameter(name = "pwd", description = "유저가 새로 설정한 비밀번호", example = "example12*!"),
//            @Parameter(name = "nickname", description = "유저 닉네임", example = "밍글밍글")
//    })

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
     * 1.9 로그인 API + JWT
     */
    //@Operation(summary = "1.9 login API", description = "1.9 로그인 API")

//    @Parameters({
//            @Parameter(name = "email", description = "회원가입에서 등록한 이메일", example = "example@mingle.com"),
//            @Parameter(name = "pwd", description = "유저가 설정한 비밀번호", example = "example12*!"),
//    })

    @PostMapping("login")
    public BaseResponse<PostLoginResponse> logIn (@RequestBody @Valid PostLoginRequest postLoginRequest) {
        try {
            if(postLoginRequest.getEmail() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if(postLoginRequest.getPwd() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
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
     * 1.10 비밀번호 초기화 API + JWT
     */

    //@Operation(summary = "1.10 Password Reset API", description = "1.10 비밀번호 초기화 API")

//    @Parameters({
//            @Parameter(name = "email", description = "회원가입에서 등록한 이메일", example = "example@mingle.com"),
//            @Parameter(name = "pwd", description = "바꾸고 싶은 비밀번호", example = "resetexample12*!"),
//            @Parameter(name = "rePwd", description = "바꾸고 싶은 비밀번호 재입력", example = "resetexample12*!")
//    })
    //재입력 비밀번호 validation 추가
    @PatchMapping("pwd")
    public BaseResponse<String> updatePwd(@RequestBody @Valid PatchUpdatePwdRequest patchUpdatePwdRequest) {
        try { //JWT로 해당 유저인지 확인 필요

            //형식적 validation
            if (patchUpdatePwdRequest.getPwd().length() == 0) {
                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
            }
            if (patchUpdatePwdRequest.getPwd().length() < 8) {
                return new BaseResponse<>(PASSWORD_LENGTH_ERROR);
            }
            if (!isRegexPassword(patchUpdatePwdRequest.getPwd())) {
                return new BaseResponse<>(PASSWORD_FORMAT_ERROR);
            }

            authService.updatePwd(patchUpdatePwdRequest);
            String result = "비밀번호 변경에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
