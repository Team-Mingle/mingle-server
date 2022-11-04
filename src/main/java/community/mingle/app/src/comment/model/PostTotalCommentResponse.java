package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Total.TotalComment;
import lombok.Getter;

@Getter
public class PostTotalCommentResponse {
    Long commentId;
    String nickname;

    public PostTotalCommentResponse(Long anonymousId, TotalComment totalComment) {
        this.commentId = totalComment.getId();
        if (anonymousId == null) {
            this.nickname = totalComment.getMember().getNickname();
        } else{
            this.nickname = "익명 " + anonymousId;
        }

    }
}
