package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Total.TotalComment;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostTotalCommentResponse {
    Long commentId;
    String nickname;
    String createdAt;

    public PostTotalCommentResponse(Long anonymousId, TotalComment totalComment) {
        this.commentId = totalComment.getId();
        if (anonymousId == 0) {
            this.nickname = totalComment.getMember().getNickname();
        } else{
            this.nickname = "익명 " + anonymousId;
        }
        createdAt = convertToDateAndTime(totalComment.getCreatedAt());


    }
}
