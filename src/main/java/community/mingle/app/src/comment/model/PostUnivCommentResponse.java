package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Univ.UnivComment;
import lombok.Getter;

@Getter
public class PostUnivCommentResponse {

    Long commentId;
    String nickname;

    public PostUnivCommentResponse(Long anonymousId, UnivComment univComment) {
        this.commentId = univComment.getId();
        if (anonymousId == null) {
            this.nickname = univComment.getMember().getNickname();
        } else{
            this.nickname = "익명 " + anonymousId;
        }

    }
}
