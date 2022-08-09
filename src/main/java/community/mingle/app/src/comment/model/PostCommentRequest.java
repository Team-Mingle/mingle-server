package community.mingle.app.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCommentRequest {

    private Long postId;
    private Long parentCommentId; //
    private String content;
    private boolean isAnonymous;
}
