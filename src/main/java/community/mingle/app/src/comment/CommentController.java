package community.mingle.app.src.comment;

import community.mingle.app.config.BaseException;
import community.mingle.app.config.BaseResponse;
import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.comment.model.PostUnivCommentRequest;
import community.mingle.app.utils.JwtService;
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
     * 전체 게시판 댓글 작성 api
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
     * 4.1 학교 게시판 댓글 작성 api
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
}



