package community.mingle.app.src.item.model;

import lombok.Getter;

@Getter
public class PostItemCommentResponse {

    Long commentId;
    String nickname;
    String createAt;

    private boolean isCommentFromAuthor;
}
