package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import lombok.Getter;

import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class TotalCommentResponse {

    private Long commentId;
    private String nickname;
    private String content;
    private int likeCount;
    private boolean isLiked;
    private boolean isMyComment;
    private String createdAt;
    private List<TotalCoCommentDTO> coCommentsList;



    public TotalCommentResponse(TotalComment totalComment, List<TotalCoCommentDTO> totalCoCommentDTOList, Long memberId) {
        commentId = totalComment.getId();

        if (totalComment.isAnonymous() == true) {
            nickname = "익명 "+totalComment.getAnonymousId();
        } else {
            nickname = totalComment.getMember().getNickname();
        }

        if (totalComment.getStatus() == PostStatus.REPORTED) {
            content = "신고된 댓글입니다";
            nickname = "(비공개됨)";
        } else if (totalComment.getStatus() == PostStatus.INACTIVE) {
            content = "삭제된 댓글입니다";
            nickname = "(비공개됨)";
        } else {
            content = totalComment.getContent();
        }

        likeCount = totalComment.getTotalCommentLikes().size();

        for (TotalCommentLike tcl : totalComment.getTotalCommentLikes()) {
            if (tcl.getMember().getId() == memberId) {
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }

        if (totalComment.getMember().getId() == memberId) {
            isMyComment = true;
        }

        createdAt = convertToDateAndTime(totalComment.getCreatedAt());
        coCommentsList = totalCoCommentDTOList;
    }

}
