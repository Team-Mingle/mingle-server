package community.mingle.app.src.auth;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.model.*;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Policy;
import community.mingle.app.src.domain.UnivEmail;
import community.mingle.app.src.domain.UnivName;
import community.mingle.app.src.member.MemberService;
import community.mingle.app.src.post.PostService;
import community.mingle.app.utils.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.BaseResponseStatus.*;
import static community.mingle.app.utils.ValidationRegex.isRegexEmail;
import static community.mingle.app.utils.ValidationRegex.isRegexPassword;


@CrossOrigin(origins = "*", allowedHeaders = "*")

@Tag(name = "auth", description = "회원가입 process 관련 API")
@RestController
@RequestMapping("/auth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
})
@RequiredArgsConstructor
public class AuthController {
    //    @Autowired
    private final AuthService authService;
    private final AuthRepository authRepository;
    private final PostService postService;
    private final MemberService memberService;


    /**
     * 1.1 학교 리스트 전송 API
     */
    @Operation(summary = "1.1 get univ list API", description = "1.1 대학교 리스트 가져오기")
    @GetMapping("/univlist")
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
    @Operation(summary = "1.2 get email domain list by univ API", description = "1.2 대학교 별 이메일 도메인 리스트 가져오기")
    @ResponseBody
    @GetMapping("/domain")
    public BaseResponse<List<GetUnivDomainResponse>> getDomain(@RequestParam int univId) {
        try {

            List<UnivEmail> findUnivDomains = authService.findDomain(univId);
            List<GetUnivDomainResponse> result = findUnivDomains.stream()
                    .map(m -> new GetUnivDomainResponse(m.getId(), m.getDomain()))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 1.3 이메일 입력 & 중복검사 API
     */

    @Operation(summary = "1.3 email duplicate check API", description = "1.3 이메일 입력 & 중복검사 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2011", description = "이메일 형식을 확인해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3017", description = "탈퇴한 사용자입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2012", description = "이미 존재하는 이메일 주소입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4012", description = "이메일 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    @ResponseBody
    @PostMapping("checkemail")
    public BaseResponse<String> verifyEmail(@RequestBody PostUserEmailRequest postUserEmailRequest) {
        System.out.println("email=" + postUserEmailRequest.getEmail());

        if (postUserEmailRequest.getEmail().isEmpty()) {
            return new BaseResponse<>(EMAIL_EMPTY_ERROR);
        }
        // 이메일 정규표현
        if (!isRegexEmail(postUserEmailRequest.getEmail())) {
            return new BaseResponse<>(EMAIL_FORMAT_ERROR);
        }
        try {
            authService.verifyEmail(postUserEmailRequest);
            return new BaseResponse<>("이메일 확인 성공");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 1.4 인증코드 전송 API
     */
    @Operation(summary = "1.4 email verification code send API", description = "1.4 이메일 인증코드 전송 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2011", description = "이메일 형식을 확인해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4013", description = "인증번호 생성에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4014", description = "인증번호 전송에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    @PostMapping("sendcode")
    public BaseResponse<String> sendCode(@RequestBody PostEmailRequest req) {
        try {
            if (req.getEmail().isEmpty()) {
                return new BaseResponse<>(EMAIL_EMPTY_ERROR);
            }
            if (!isRegexEmail(req.getEmail())) { //이메일 형식(정규식) 검증
                return new BaseResponse<>(EMAIL_FORMAT_ERROR);
            }
            authService.sendCode(req);
            //return ResponseEntity.ok().build();
            String result = "인증번호가 전송되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 1.5 인증 코드 검사 API
     */
    @Operation(summary = "1.5 email verification code check API", description = "1.5 이메일 인증코드 검사 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2011", description = "이메일 형식을 확인해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2013", description = "인증번호가 일치하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2019", description = "인증번호를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3015", description = "인증번호 입력시간이 초과되었습니다" , content = @Content (schema = @Schema(hidden = true))),
    })
    @ResponseBody
    @PostMapping("checkcode")
    public BaseResponse<String> verifyCode(@RequestBody PostCodeRequest code) {
        try {
            if (code.getEmail().isEmpty()) {
                return new BaseResponse<>(EMAIL_EMPTY_ERROR);
            }
            if (code.getCode().isEmpty()) {
                return new BaseResponse<>(CODE_EMPTY_ERROR);
            }
            if (!isRegexEmail(code.getEmail())) { //이메일 형식(정규식) 검증
                return new BaseResponse<>(EMAIL_FORMAT_ERROR);
            }
            authService.authCode(code.getEmail(), code.getCode());
//            CodeResponse codeRes = emailService.authCode(code.getCode(), code.getEmail());
            String result = "인증에 성공하였습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 1.6 개인정보 처리방침
     */
    @Operation(summary = "1.6 get privacy policy API", description = "1.6 개인정보처리방침 가져오기 API ")
    @ApiResponses({
            @ApiResponse(responseCode = "3011", description = "약관을 불러오는데 실패하였습니다.")
    })
    @GetMapping("terms/privacy")
    public BaseResponse<String> getPrivacyTerms() {
        try {
//            String filePath = "src/main/java/community/mingle/app/config/personalinfoterms";
//            FileInputStream fileStream = null;
//
//            fileStream = new FileInputStream(filePath);
//            byte[] readBuffer = new byte[fileStream.available()];
//            while (fileStream.read(readBuffer) != -1) {
//            }
//            fileStream.close(); //스트림 닫기
//            return new BaseResponse<>(new String(readBuffer));
            Policy privacyTerms = authRepository.findTerms(Long.valueOf(1));
            return new BaseResponse<>(privacyTerms.getContent());
        } catch (Exception e) {
            return new BaseResponse<>(FAILED_TO_GET_TERMS);
        }
    }


    /**
     * 1.7 서비스이용약관
     */
    @Operation(summary = "1.7 get service policy API", description = "1.7 서비스이용약관 가져오기 API")
    @ApiResponses({
            @ApiResponse(responseCode = "3011", description = "약관을 불러오는데 실패하였습니다.")
    })
    @GetMapping("terms/service")
    public BaseResponse<String> getServiceTerms() {
        try {
//            String filePath = "src/main/java/community/mingle/app/config/serviceUsageTerms";
//            FileInputStream fileStream = null;
//
//            fileStream = new FileInputStream(filePath);
//            byte[] readBuffer = new byte[fileStream.available()];
//            while (fileStream.read(readBuffer) != -1) {
//            }
//            fileStream.close();
//            return new BaseResponse<>(new String(readBuffer));
            Policy privacyTerms = authRepository.findTerms(Long.valueOf(2));
            return new BaseResponse<>(privacyTerms.getContent());
        } catch (Exception e) {
            return new BaseResponse<>(FAILED_TO_GET_TERMS);
        }
    }



    /**
     * 1.8 회원가입 API + JWT
     */
    @Operation(summary = "1.8 sign up API", description = "1.8 회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2011", description = "이메일 형식을 확인해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2012", description = "이미 존재하는 이메일 주소입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2014", description = "비밀번호를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2015", description = "비밀번호가 너무 짧습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2016", description = "비밀번호는 영문,숫자를 포함해야 합니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2017", description = "이미 존재하는 닉네임입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2018", description = "존재하지 않는 학교 id 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3010", description = "회원가입에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3017", description = "탈퇴한 사용자입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4011", description = "비밀번호 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4012", description = "이메일 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    @ResponseBody
    @PostMapping("signup")
    public BaseResponse<PostSignupResponse> createMember(@RequestBody PostSignupRequest postSignupRequest) {
        //이메일 빔
        if (postSignupRequest.getEmail().isEmpty()) {
            return new BaseResponse<>(EMAIL_EMPTY_ERROR);
        }
        //이메일 형식(정규식) 검증 (new)
        if (!isRegexEmail(postSignupRequest.getEmail())) {
            return new BaseResponse<>(EMAIL_FORMAT_ERROR);
        }
        // 비밀번호 빔
        if (postSignupRequest.getPwd().isEmpty()) {
            return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
        }
        //비밀번호 길이
        if (postSignupRequest.getPwd().length() < 6) {
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
    @Operation(summary = "1.9 login API", description = "1.9 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2014", description = "비밀번호를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3011", description = "존재하지 않는 이메일이거나 비밀번호가 틀렸습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3012", description = "JWT 발급에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3017", description = "탈퇴한 사용자입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3018", description = "신고된 사용자입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4011", description = "비밀번호 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4012", description = "이메일 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    @PostMapping("login")
    public BaseResponse<PostLoginResponse> logIn(@RequestBody PostLoginRequest postLoginRequest) {
        try {
            if (postLoginRequest.getEmail().isEmpty()) {
                return new BaseResponse<>(EMAIL_EMPTY_ERROR);
            }
            if (postLoginRequest.getPwd().isEmpty()) {
                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
            }
            PostLoginResponse postloginResponse = authService.logIn(postLoginRequest);
            return new BaseResponse<>(postloginResponse);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 1.10 비밀번호 초기화 API + JWT
     */
    @Operation(summary = "1.10 resetPwd API", description = "1.10 비밀번호 초기화 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2014", description = "비밀번호를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2015", description = "비밀번호가 너무 짧습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2016", description = "비밀번호는 영문,숫자를 포함해야 합니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3013", description = "비밀번호 변경에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4011", description = "비밀번호 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4012", description = "이메일 암호화에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    @PatchMapping("pwd")
    public BaseResponse<String> resetPwd(@RequestBody PatchUpdatePwdRequest patchUpdatePwdRequest) {
        try { //JWT로 해당 유저인지 확인 필요

            //형식적 validation
            if (patchUpdatePwdRequest.getPwd().isEmpty()) {
                return new BaseResponse<>(PASSWORD_EMPTY_ERROR);
            }
            if (patchUpdatePwdRequest.getPwd().length() < 6) {
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



    /**
     * 1.11 비밀번호 재설정용 인증번호 보내기
     */
    @Operation(summary = "1.11 sendCodeForPwdReset api", description = "1.11 비밀번호 재설정 인증번호 보내기 API")
    @ApiResponses({
            @ApiResponse(responseCode = "2010", description = "이메일을 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2011", description = "이메일 형식을 확인해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2020", description = "회원 정보를 찾을 수 없습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4014", description = "인증번호 전송에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    @PostMapping("sendcode/pwd")
    public BaseResponse<String> sendCodeForPwdReset (@RequestBody PostEmailRequest req) {
        if (req.getEmail().isEmpty()) {
            return new BaseResponse<>(EMAIL_EMPTY_ERROR);
        }
        if (!isRegexEmail(req.getEmail())) {
            return new BaseResponse<>(EMAIL_FORMAT_ERROR);
        }
        try {
            authService.sendCodeForPwd(req);
            String result = "인증번호가 전송되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 1.12 access Token 재발급 By refresh Token
     **/
    @Operation(summary = "1.12 reissueAccessToken api", description = "1.12 AccessToken, RefreshToken 재발급 API")
    @PostMapping("refresh-token")
    @Parameter(name = "Authorization", required = true, description = "RefreshToken", in = ParameterIn.HEADER)
    public BaseResponse<ReissueAccessTokenDTO> reissueAccessToken(@RequestHeader(value = "Authorization") String refreshToken, @RequestBody TokenReIssueDTO tokenReIssueDTO ) {
        try {
            return new BaseResponse<>(authService.reissueAccessToken(refreshToken, tokenReIssueDTO.getEmail()));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 1.13 fcm Token 리프레시 api
     */
    @Operation(summary = "1.13 fcm token refresh api", description = "1.13 fcm Token 리프레시 api")
    @PatchMapping("fcmtoken")
    public BaseResponse<String> refreshFcmToken(@RequestBody FcmTokenRequest fcmTokenRequest) {
        try {
            authService.refreshFcmToken(fcmTokenRequest);
            return new BaseResponse<>("fcmToken 재발급에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PatchMapping("/report-member")
    public void executeMember(@RequestParam Long memberId) throws IOException {
        postService.executeMember(memberId);
    }



    /**
     * 1.15 유저 글/댓글 INACTIVE api
     */
    @PatchMapping("/withdraw")
    public BaseResponse<String> disablePosts(@RequestParam Long memberId) {
        try {
            memberService.disablePosts(memberId);
            return new BaseResponse<>("숙청 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}

