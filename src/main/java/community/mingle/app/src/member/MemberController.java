package community.mingle.app.src.member;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.member.model.PatchNicknameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "member", description = "유저 관련 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * 2.1 닉네임 수정 API
     */
    @PatchMapping("/nickname")
    @Operation(summary = "2.1 modifyNickname API", description = "2.1 닉네임 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2017", description = "중복된 닉네임입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2020", description = "닉네임 수정에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> modifyNickname(@RequestBody PatchNicknameRequest patchNicknameRequest) {

        try {
            memberService.modifyNickname(patchNicknameRequest.getNickname());
            return new BaseResponse<>("닉네임 변경에 성공하였습니다.");

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
