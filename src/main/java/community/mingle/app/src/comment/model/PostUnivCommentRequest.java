package community.mingle.app.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class PostUnivCommentRequest {

    @NotNull
    private Long postId;
    private Long parentCommentId; //
    private Long mentionId;
    @NotBlank
    private String content;
    @NotNull
    private boolean isAnonymous;

}
