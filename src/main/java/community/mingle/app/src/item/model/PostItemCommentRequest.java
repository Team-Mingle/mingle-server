package community.mingle.app.src.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class PostItemCommentRequest {

    @NotNull
    private Long itemId;
    private Long parentCommentId;
    private Long mentionId;
    @NotEmpty
    private String content;
    @NotNull
    private boolean isAnonymous;

}
