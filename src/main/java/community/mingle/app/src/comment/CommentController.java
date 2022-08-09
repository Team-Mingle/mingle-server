package community.mingle.app.src.comment;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.auth.model.PostSignupResponse;
import community.mingle.app.src.comment.model.PostCommentLikesTotalResponse;
import community.mingle.app.src.comment.model.PostCommentLikesUnivResponse;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.model.GetTotalBestPostsResponse;
import community.mingle.app.src.post.model.GetUnivBestResponse;
import community.mingle.app.src.post.model.PostLikesUnivResponse;
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
     * 3.05 통합 게시물 댓글 좋아요 api
     */
    @Operation(summary = "3.05 likesTotalComment api", description = "3.05 통합 게시물 댓글 좋아요 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/total/likes")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostCommentLikesTotalResponse> likesTotalComment (@RequestParam Long commentIdx){
        try{
            return new BaseResponse<>(commentService.likesTotalComment(commentIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 3.06 학교 게시물 댓글 좋아요 api
     */
    @Operation(summary = "3.06 likesUnivComment  api", description = "학교 게시물 댓글 좋아요 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/univ/likes")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<PostCommentLikesUnivResponse> likesUnivComment (@RequestParam Long commentIdx){
        try{
            return new BaseResponse<>(commentService.likesUnivComment(commentIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 통합 게시물 좋아요  취소 api
     */
    @Operation(summary = "UnlikeTotalComment API", description = "통합 게시물 댓글 취소 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @DeleteMapping("/total/unlike")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<String> unlikeTotalComment (@RequestParam Long commentIdx){
        try{
            commentService.unlikeTotalComment(commentIdx);
            String result = "좋아요가 취소되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 학교 게시물 댓글 좋아요 취소 api
     */
    @Operation(summary = "UnlikeUnivComment API", description = "통합 게시물 댓글 취소 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @DeleteMapping("/univ/unlike")
    @ApiResponses ({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
    })
    public BaseResponse<String> unlikeUnivComment (@RequestParam Long commentIdx){
        try{
            commentService.unlikeUnivComment(commentIdx);
            String result = "좋아요가 취소되었습니다";
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }




}



