package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.ItemComment;
import community.mingle.app.src.domain.ItemCommentLike;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivCommentLike;
import community.mingle.app.src.domain.UserRole;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class CommentResponse {

    private Long commentId;
    private String nickname;
    private final String content;
    private final int likeCount;
    private boolean isLiked;
    private boolean isMyComment;
    private final boolean isCommentFromAuthor;
    private final boolean isCommentDeleted;
    private final boolean isCommentReported;
    private final String createdAt;
    private final List<CoCommentDTO> coCommentsList;
    private final boolean isAdmin;


    //total
    public CommentResponse(TotalComment totalComment, List<CoCommentDTO> totalCoCommentDTOList, Long memberId, Long authorId) {
        commentId = totalComment.getId();
        Long commentWriter = totalComment.getMember().getId();

        this.commentId = totalComment.getId();
        if (!totalComment.isAnonymous() && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = totalComment.getMember().getNickname();
        } else if (totalComment.isAnonymous() && totalComment.getAnonymousId() != 0L) {
            this.nickname = "익명 " + totalComment.getAnonymousId();
        } else if (!totalComment.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = totalComment.getMember().getNickname() + "(글쓴이)";
        } else if ((totalComment.isAnonymous() && Objects.equals(commentWriter, authorId))) {
            this.nickname = "익명(글쓴이)";
        }
        if (totalComment.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }
        if (totalComment.getStatus() == PostStatus.REPORTED) {
            content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (totalComment.getStatus() == PostStatus.INACTIVE) {
            content = "삭제된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (totalComment.getStatus() == PostStatus.DELETED) {
            content = "운영규칙 위반에 따라 삭제된 글입니다.";
            nickname = "(비공개됨)";
        } else {
            content = totalComment.getContent();
        }
        likeCount = totalComment.getTotalCommentLikes().size();
        for (TotalCommentLike tcl : totalComment.getTotalCommentLikes()) {
            if (Objects.equals(tcl.getMember().getId(), memberId)) {
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }
        if (Objects.equals(commentWriter, memberId)) {
            isMyComment = true;
        }
        isCommentFromAuthor = Objects.equals(commentWriter, authorId);
        isCommentDeleted = totalComment.getStatus() == PostStatus.INACTIVE;
        isCommentReported = totalComment.getStatus() == PostStatus.REPORTED;

        createdAt = convertToDateAndTime(totalComment.getCreatedAt());
        coCommentsList = totalCoCommentDTOList;
        isAdmin = totalComment.getMember().getRole().equals(UserRole.ADMIN);
    }


    //univ
    public CommentResponse(UnivComment c, List<CoCommentDTO> cc, Long memberId, Long authorId) { //univ
        Long commentWriter = c.getMember().getId();
        commentId = c.getId();

        this.commentId = c.getId();
        if (!c.isAnonymous() && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = c.getMember().getNickname();
        } else if (c.isAnonymous() && c.getAnonymousId() != 0L) {
            this.nickname = "익명 " + c.getAnonymousId();
        } else if (!c.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = c.getMember().getNickname() + "(글쓴이)";
        } else if (c.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }
        if (c.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }

        if (c.getStatus() == PostStatus.REPORTED) {
            content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (c.getStatus() == PostStatus.INACTIVE) {
            content = "삭제된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (c.getStatus() == PostStatus.DELETED) {
            content = "운영규칙 위반에 따라 삭제된 글입니다.";
            nickname = "(비공개됨)";
        } else {
            content = c.getContent();
        }

        likeCount = c.getUnivCommentLikes().size();

        for (UnivCommentLike ucl : c.getUnivCommentLikes()) { //영속성
            if (Objects.equals(ucl.getMember().getId(), memberId)) { //배치사이즈?
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
        isCommentFromAuthor = Objects.equals(commentWriter, authorId);

        isCommentDeleted = c.getStatus() == PostStatus.INACTIVE;

        isCommentReported = c.getStatus() == PostStatus.REPORTED;
        createdAt = convertToDateAndTime(c.getCreatedAt());
        coCommentsList = cc;
        isAdmin = c.getMember().getRole().equals(UserRole.ADMIN);
    }

    public CommentResponse(ItemComment c, List<CoCommentDTO> cc, Long memberId, Long authorId) { //univ
        Long commentWriter = c.getMember().getId();
        commentId = c.getId();

        this.commentId = c.getId();
        if (!c.isAnonymous() && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = c.getMember().getNickname();
        } else if (c.isAnonymous() && c.getAnonymousId() != 0L) {
            this.nickname = "익명 " + c.getAnonymousId();
        } else if (!c.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = c.getMember().getNickname() + "(글쓴이)";
        } else if (c.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }

        if (c.getStatus() == PostStatus.REPORTED) {
            content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (c.getStatus() == PostStatus.INACTIVE) {
            content = "삭제된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (c.getStatus() == PostStatus.DELETED) {
            content = "운영규칙 위반에 따라 삭제된 글입니다.";
            nickname = "(비공개됨)";
        } else {
            content = c.getContent();
        }

        likeCount = c.getItemCommentLikes().size();

        for (ItemCommentLike icl : c.getItemCommentLikes()) { //영속성
            if (Objects.equals(icl.getMember().getId(), memberId)) { //배치사이즈?
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
        isCommentFromAuthor = Objects.equals(commentWriter, authorId);

        isCommentDeleted = c.getStatus() == PostStatus.INACTIVE;

        isCommentReported = c.getStatus() == PostStatus.REPORTED;
        createdAt = convertToDateAndTime(c.getCreatedAt());
        coCommentsList = cc;
        isAdmin = c.getMember().getRole().equals(UserRole.ADMIN);
    }
}
