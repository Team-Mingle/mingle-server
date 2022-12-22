package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class TotalCommentResponse {

    private Long commentId;
    private String nickname;
    private String content;
    private int likeCount;
    private boolean isLiked;
    private boolean isMyComment;

    //11/25 추가
    private boolean isCommentFromAuthor;
    private boolean isCommentDeleted;
    private boolean isCommentReported;
    private String createdAt;
    private List<TotalCoCommentDTO> coCommentsList;



    public TotalCommentResponse(TotalComment totalComment, List<TotalCoCommentDTO> totalCoCommentDTOList, Long memberId, Long authorId) {
        commentId = totalComment.getId();
        Long commentWriter = totalComment.getMember().getId();

        this.commentId = totalComment.getId();
        if (totalComment.isAnonymous() == false && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = totalComment.getMember().getNickname();
        } else if (totalComment.isAnonymous() && totalComment.getAnonymousId() != 0L){
            this.nickname = "익명 " + totalComment.getAnonymousId();
        } else if (totalComment.isAnonymous() == false && Objects.equals(commentWriter, authorId)) {
            this.nickname = totalComment.getMember().getNickname() + "(글쓴이)";
        } else if ((totalComment.isAnonymous() && Objects.equals(commentWriter, authorId))){
            this.nickname = "익명(글쓴이)";
        }
//        if (totalComment.isAnonymous() == true) {
//            nickname = "익명 "+totalComment.getAnonymousId();
//        } else {
//            nickname = totalComment.getMember().getNickname();
//        }

        if (totalComment.getStatus() == PostStatus.REPORTED) {
            content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (totalComment.getStatus() == PostStatus.INACTIVE) {
            content = "삭제된 댓글입니다.";
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

        if (Objects.equals(commentWriter, memberId)) {
            isMyComment = true;
        }

        if (Objects.equals(commentWriter, authorId)){
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }

        if (totalComment.getStatus() == PostStatus.INACTIVE) {
            isCommentDeleted = true;
        } else {
            isCommentDeleted = false;
        }

        if (totalComment.getStatus() == PostStatus.REPORTED) {
            isCommentReported = true;
        } else {
            isCommentReported = false;
        }

        createdAt = convertToDateAndTime(totalComment.getCreatedAt());
        coCommentsList = totalCoCommentDTOList;
    }

}
