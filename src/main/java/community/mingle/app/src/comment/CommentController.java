package community.mingle.app.src.comment;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.model.PostSignupResponse;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.model.GetTotalBestPostsResponse;
import community.mingle.app.src.post.model.GetUnivBestResponse;
import community.mingle.app.utils.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;
    private final CommentRepository commentRepository;


    /**
     * 전체 게시판 댓글 작성 api
     */
    @PostMapping("/total")
    public BaseResponse<Long> createComment(@RequestBody @Valid PostTotalCommentRequest postTotalCommentRequest) {
        try {
            Long id = commentService.createComment(postTotalCommentRequest);
            return new BaseResponse<>(id);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 통합 게시물 댓글 삭제 api
     */
    @Operation(summary = "4.07 deleteTotalComment API", description = "4.07 통합 게시물 댓글 삭제 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/total/status/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteTotalComment (@PathVariable Long id){

        try{
            commentService.deleteTotalComment(id);
            String result = "댓글 삭제에 성공하였습니다.";
            return new BaseResponse<>(result);

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 학교 게시물 댓글 삭제 api
     */
    @Operation(summary = "4.08 deleteUnivComment API", description = "4.08 학교 게시물 댓글 삭제 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/univ/status/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<String> deleteUnivComment (@PathVariable Long id){

        try{
            commentService.deleteUnivComment(id);
            String result = "댓글 삭제에 성공하였습니다.";
            return new BaseResponse<>(result);

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}



