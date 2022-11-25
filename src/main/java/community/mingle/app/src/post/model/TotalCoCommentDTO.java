package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class TotalCoCommentDTO {

    private Long commentId;
    private Long parentCommentId;
    private String mention;
    private String nickname;
    private String content;
    private int likeCount;
    private boolean isLiked;
    private boolean isMyComment;
    private String createdAt;

    public TotalCoCommentDTO(TotalComment coComment, TotalComment mention, Long memberId) {
        this.commentId = coComment.getId();
        this.parentCommentId = coComment.getParentCommentId();

        if (coComment.isAnonymous() == true) {
            this.nickname = "익명 "+coComment.getAnonymousId();
        } else{
            this.nickname = coComment.getMember().getNickname();
        }

        if (mention.isAnonymous() == true) {
            this.mention = "익명 "+mention.getAnonymousId();
        }else{
            this.mention = mention.getMember().getNickname();
        }

        if (coComment.getStatus() == PostStatus.REPORTED) {
            this.content = "신고된 댓글입니다";
        } else if (coComment.getStatus() == PostStatus.INACTIVE) {
            this.content = "삭제된 댓글입니다";
        } else {
            this.content = coComment.getContent();
        }

        this.likeCount = coComment.getTotalCommentLikes().size();


        for (TotalCommentLike tpl : coComment.getTotalCommentLikes()) {
            if (tpl.getMember().getId() == memberId) {
                this.isLiked = true;
                break;
            } else {
                this.isLiked = false;
            }
        }

        if (coComment.getMember().getId() == memberId) {
            isMyComment = true;
        }

        this.createdAt = convertToDateAndTime(coComment.getCreatedAt());

    }
}
