package community.mingle.app.src.comment;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.utils.JwtService;
import community.mingle.app.src.comment.model.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "comment", description = "댓글 관련 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "토큰을 입력해주세요.(앞에 'Bearer ' 포함)./  토큰을 입력해주세요. / 잘못된 토큰입니다. / 토큰이 만료되었습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content (schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다.", content = @Content (schema = @Schema(hidden = true)))
})
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    /**
     * 4.1 전체 게시판 댓글 작성 api
     */
    @Operation(summary = "4.1 createTotalComment api", description = "4.1 전체 게시판 댓글 작성 api")
    @PostMapping("/total")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4040", description = "잘못된 parentCommentId / mentionId 입니다.", content = @Content (schema = @Schema(hidden = true)))
    })
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
    @Operation(summary = "4.2 createUnivComment api", description = "4.2 학교 게시판 댓글 작성 api")
    @PostMapping("/univ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "3035", description = "게시물이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4040", description = "잘못된 parentCommentId / mentionId 입니다.", content = @Content (schema = @Schema(hidden = true)))
    })
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
    @Operation(summary = "4.3 likesTotalComment api", description = "4.3 통합 게시물 댓글 좋아요 api")
    @PostMapping("/total/likes")
    @ApiResponses ({
            @ApiResponse(responseCode = "4035", description = "댓글이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4050", description = "삭제되거나 신고된 댓글 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 좋아요를 눌렀어요.", content = @Content (schema = @Schema(hidden = true))),
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
    @Operation(summary = "4.4 likesUnivComment api", description = "학교 게시물 댓글 좋아요 api")
    @PostMapping("/univ/likes")
    @ApiResponses ({
            @ApiResponse(responseCode = "4035", description = "댓글이 존재하지 않습니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "4050", description = "삭제되거나 신고된 댓글 입니다.", content = @Content (schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "3060", description = "이미 좋아요를 눌렀어요.", content = @Content (schema = @Schema(hidden = true))),
    })
    public BaseResponse<PostCommentLikesUnivResponse> likesUnivComment (@RequestParam Long commentIdx){
        try{
            return new BaseResponse<>(commentService.likesUnivComment(commentIdx));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 4.5 통합 게시물 좋아요 취소 api
     */
    @Operation(summary = "4.5 UnlikeTotalComment API", description = "통합 게시물 댓글 좋아요 취소 api")
    @DeleteMapping("/total/unlike")
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
    @Operation(summary = "4.6 UnlikeUnivComment API", description = "학교 게시물 댓글 좋아요 취소 api")
    @DeleteMapping("/univ/unlike")
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
    @Operation(summary = "4.7 deleteTotalComment API", description = "4.7 통합 게시물 댓글 삭제 API")
    @PatchMapping("/total/status/{id}")
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
    @Operation(summary = "4.8 deleteUnivComment API", description = "4.8 학교 게시물 댓글 삭제 API")
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



