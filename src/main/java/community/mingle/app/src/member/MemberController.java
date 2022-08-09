package community.mingle.app.src.member;


import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.member.model.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "member", description = "유저 관련 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * 2.6 유저 삭제 API
     */
    @PatchMapping("/delete")
    @Operation(summary = "2.6  deleteMemberIdx API", description = "2.6 유저 삭제 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER)
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<String> deleteMember() {
        try {
            memberService.deleteMember();
            String result = "유저가 삭제되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

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

    @GetMapping("/posts/total")
    public BaseResponse<List<TotalMyPostDTO>> getTotalPosts() {
        try {
            List<TotalPost> totalPosts = memberService.getTotalPosts();
            List<TotalMyPostDTO> result = totalPosts.stream()
                    .map(p -> new TotalMyPostDTO(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/posts/univ")
    public BaseResponse<List<UnivMyPostDTO>> getUnivPosts() {
        try {
            List<UnivPost> univPosts = memberService.getUnivPosts();
            List<UnivMyPostDTO> result = univPosts.stream()
                    .map(p -> new UnivMyPostDTO(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/comments/total")
    public BaseResponse<List<TotalMyCommentDTO>> getTotalComments() {
        try {
            List<TotalPost> totalComments = memberService.getTotalComments();
            List<TotalMyCommentDTO> result = totalComments.stream()
                    .map(p -> new TotalMyCommentDTO(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/comments/univ")
    public BaseResponse<List<UnivMyCommentDTO>> getUnivComments() {
        try {
            List<UnivPost> univComments = memberService.getUnivComments();
            List<UnivMyCommentDTO> result = univComments.stream()
                    .map(p -> new UnivMyCommentDTO(p))
                    .collect(Collectors.toList());
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/report")
    public BaseResponse<ReportDTO> createReport(@RequestBody @Valid ReportRequest reportRequest) {
        try {
            Member reportedMember = memberService.findReportedMember(reportRequest);
            ReportDTO reportDTO = memberService.createReport(reportRequest, reportedMember);
            memberService.checkReportedMember(reportedMember);
            memberService.checkReportedPost(reportRequest);
            return new BaseResponse<>(reportDTO);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
