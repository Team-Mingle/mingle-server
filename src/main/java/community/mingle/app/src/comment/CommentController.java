package community.mingle.app.src.comment;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.utils.JwtService;
import community.mingle.app.src.comment.model.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;
    private final CommentRepository commentRepository;

    /**
     * 4.1 전체 게시판 댓글 작성 api
     */
    @PostMapping("/total")
    public BaseResponse<Long> createTotalComment(@RequestBody PostTotalCommentRequest postTotalCommentRequest) {
        try {
            Long id = commentService.createTotalComment(postTotalCommentRequest);
            return new BaseResponse<>(id);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 4.2  학교 게시판 댓글 작성 api
     */
    @PostMapping("/univ")
    public BaseResponse<Long> createUnivComment(@RequestBody PostUnivCommentRequest univCommentRequest) {
        try {
            Long id = commentService.createUnivComment(univCommentRequest);
            return new BaseResponse<>(id);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 4.3  통합 게시물 댓글 좋아요 api
     */
    @Operation(summary = "3.05 likesTotalComment api", description = "3.05 통합 게시물 댓글 좋아요 api")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PostMapping("/total/likes")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content(schema = @Schema(hidden = true))),
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
     * 4.4  학교 게시물 댓글 좋아요 api
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
     * 4.5 통합 게시물 좋아요  취소 api
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
     * 4.6 학교 게시물 댓글 좋아요 취소 api
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


    /**
     * 4.7 통합 게시물 댓글 삭제 api
     */
    @Operation(summary = "4.07 deleteTotalComment API", description = "4.07 통합 게시물 댓글 삭제 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/total/status/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3025", description = "게시물 삭제를 실패했습니다.", content = @Content (schema = @Schema(hidden = true)))
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
     * 4.8 학교 게시물 댓글 삭제 api
     */
    @Operation(summary = "4.08 deleteUnivComment API", description = "4.08 학교 게시물 댓글 삭제 API")
    @Parameter(name = "X-ACCESS-TOKEN", required = true, description = "유저의 JWT", in = ParameterIn.HEADER) //swagger
    @PatchMapping("/univ/status/{id}")

    public BaseResponse<String> deleteUnivComment (@PathVariable Long id){
            try {
                commentService.deleteUnivComment(id);
                String result = "댓글 삭제에 성공하였습니다.";
                return new BaseResponse<>(result);

            } catch (BaseException exception) {
                return new BaseResponse<>(exception.getStatus());
            }
        }

}



