package community.mingle.app.src.member;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostScrap;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import community.mingle.app.src.member.model.PatchNicknameRequest;
import community.mingle.app.src.member.model.TotalPostScrapDTO;
import community.mingle.app.src.member.model.UnivPostScrapDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2017", description = "중복된 닉네임입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2020", description = "닉네임 수정에 실패하였습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> modifyNickname(@RequestBody PatchNicknameRequest patchNicknameRequest) {

        try {
            memberService.modifyNickname(patchNicknameRequest.getNickname());
            return new BaseResponse<>("닉네임 변경에 성공하였습니다.");

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 2.5 내가 스크랩 한 글 (대학) API
     */
    @GetMapping("/scraps/univ")
    public BaseResponse<List<UnivPostScrapDTO>> getUnivScraps() {
        try {
            List<UnivPost> univPosts = memberService.getUnivScraps();
            List<UnivPostScrapDTO> result = univPosts.stream()
                    .map(post -> new UnivPostScrapDTO(post))
                    .collect(Collectors.toList());

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 2.6 내가 스크랩 한 글 (전체) API
     */
    @GetMapping("/scraps/total")
    public BaseResponse<List<TotalPostScrapDTO>> getTotalScraps() {
        try {
            List<TotalPost> totalPosts = memberService.getTotalScraps();
            List<TotalPostScrapDTO> result = totalPosts.stream()
                    .map(post -> new TotalPostScrapDTO(post))
                    .collect(Collectors.toList());

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
