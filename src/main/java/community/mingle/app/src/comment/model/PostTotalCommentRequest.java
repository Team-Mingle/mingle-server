package community.mingle.app.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class PostTotalCommentRequest {

    @NotNull
    private Long postId;
    private Long parentCommentId; //
    private Long mentionId;
    @NotEmpty
    private String content;
    @NotNull
    private boolean isAnonymous;

}
