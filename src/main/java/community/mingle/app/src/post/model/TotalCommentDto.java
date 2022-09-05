package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import lombok.Getter;

import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;
import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class TotalCommentDto {

    private Long commentId;
    private String content;
    private int likeCount;
    private String nickname;
    private String createdAt;

    private boolean isLiked;
    private List<TotalCocommentDto> totalCocommentDtoList;



    public TotalCommentDto(TotalComment totalComment, List<TotalCocommentDto> totalCocommentDtoList, Long memberId) {
        this.commentId = totalComment.getId();

        if (totalComment.getStatus() == PostStatus.REPORTED) {
            this.content = "신고된 댓글입니다";
        } else if (totalComment.getStatus() == PostStatus.INACTIVE) {
            this.content = "삭제된 댓글입니다";
        } else {
            this.content = totalComment.getContent();
        }

        this.likeCount = totalComment.getTotalCommentLikes().size();

        if (totalComment.isAnonymous() == true) {
            this.nickname = "익명 "+totalComment.getAnonymousId();
        } else {
            this.nickname = totalComment.getMember().getNickname();
        }

        this.createdAt = convertToDateAndTime(totalComment.getCreatedAt());

        for (TotalCommentLike tcl : totalComment.getTotalCommentLikes()) {
            if (tcl.getMember().getId() == memberId) {
                this.isLiked = true;
                break;
            } else {
                this.isLiked = false;
            }
        }

        this.totalCocommentDtoList = totalCocommentDtoList;
    }

}
