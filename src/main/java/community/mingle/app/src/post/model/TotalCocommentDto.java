package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalComment;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class TotalCocommentDto {

    private Long commentId;
    private Long parentCommentId;
    private String mention;
    private String content;
    private int likeCount;
    private String nickname;
    private String createdAt;

    public TotalCocommentDto(TotalComment coComment, TotalComment comment) {
        this.commentId = coComment.getId();
        this.parentCommentId = coComment.getParentCommentId();
        if (comment.isAnonymous() == true) {
            this.mention = "익명 "+comment.getAnonymousId();
        }else{
            this.mention = comment.getMember().getNickname();
        }
        this.content = coComment.getContent();
        this.likeCount = coComment.getTotalCommentLikes().size();
        if (coComment.isAnonymous() == true) {
            this.nickname = "익명 "+coComment.getAnonymousId();
        } else{
            this.nickname = coComment.getMember().getNickname();
        }
        this.createdAt = convertLocaldatetimeToTime(coComment.getCreatedAt());
    }
}
