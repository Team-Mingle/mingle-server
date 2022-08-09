package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import community.mingle.app.src.domain.Total.TotalPostLike;
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
    private boolean isLiked;

    public TotalCocommentDto(TotalComment coComment, TotalComment comment, Long memberId) {
        this.commentId = coComment.getId();
        this.parentCommentId = coComment.getParentCommentId();

        if (comment.isAnonymous() == true) {
            this.mention = "익명 "+comment.getAnonymousId();
        }else{
            this.mention = comment.getMember().getNickname();
        }

        if (coComment.getStatus() == PostStatus.REPORTED) {
            this.content = "신고된 댓글 입니다";
        } else if (coComment.getStatus() == PostStatus.INACTIVE) {
            this.content = "삭제된 댓글 입니다";
        } else {
            this.content = coComment.getContent();
        }

        this.likeCount = coComment.getTotalCommentLikes().size();

        if (coComment.isAnonymous() == true) {
            this.nickname = "익명 "+coComment.getAnonymousId();
        } else{
            this.nickname = coComment.getMember().getNickname();
        }
        this.createdAt = convertLocaldatetimeToTime(coComment.getCreatedAt());

        for (TotalCommentLike tpl : coComment.getTotalCommentLikes()) {
            if (tpl.getMember().getId() == memberId) {
                this.isLiked = true;
                break;
            } else {
                this.isLiked = false;
            }
        }
    }
}
