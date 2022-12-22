package community.mingle.app.src.post.model;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivCommentLike;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class UnivCommentResponse {

    private Long commentId;
    private String nickname;
    private String content;
    private int likeCount;
    private boolean isLiked;
    private boolean isMyComment;
    private boolean isCommentFromAuthor;
    private boolean isCommentDeleted;
    private boolean isCommentReported;
    private String createdAt;
    private List<UnivCoCommentDTO> coCommentsList;


    public UnivCommentResponse(UnivComment c, List<UnivCoCommentDTO> cc, Long memberId, Long authorId) {
        Long commentWriter = c.getMember().getId();
        commentId = c.getId();
        
        this.commentId = c.getId();
        if (c.isAnonymous() == false&& !(Objects.equals(commentWriter, authorId))) {
            this.nickname = c.getMember().getNickname();
        } else if (c.isAnonymous() && c.getAnonymousId() != 0L){
            this.nickname = "익명 " + c.getAnonymousId();
        } else if (!c.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = c.getMember().getNickname() + "(글쓴이)";
        } else if (c.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }
//        if (c.isAnonymous() == true) {
//            this.nickname = "익명 "+c.getAnonymousId();
//        } else {
//            this.nickname = c.getMember().getNickname();
//        }

        if (c.getStatus() == PostStatus.REPORTED) {
            content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (c.getStatus() == PostStatus.INACTIVE) {
            content = "삭제된 댓글입니다.";
            nickname = "(비공개됨)";
        } else {
            content = c.getContent();
        }

        likeCount = c.getUnivCommentLikes().size();

        for (UnivCommentLike ucl : c.getUnivCommentLikes()) { //영속성
            if (ucl.getMember().getId() == memberId) { //배치사이즈?
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }
        if (Objects.equals(commentWriter, memberId)) {
            isMyComment = true;
        }
        if (Objects.equals(commentWriter, memberId)) {
            isMyComment = true;
        }

        if (Objects.equals(commentWriter, authorId)){
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }

        if (c.getStatus() == PostStatus.INACTIVE) {
            isCommentDeleted = true;
        } else {
            isCommentDeleted = false;
        }

        if (c.getStatus() == PostStatus.REPORTED) {
            isCommentReported = true;
        } else {
            isCommentReported = false;
        }


        createdAt = convertToDateAndTime(c.getCreatedAt());
        coCommentsList = cc;
    }
}
