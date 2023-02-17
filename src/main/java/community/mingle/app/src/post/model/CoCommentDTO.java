package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivCommentLike;
import lombok.Getter;

import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class CoCommentDTO {

    private Long commentId;
    private Long parentCommentId;
    private String mention; //멘션 추가
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


    //total
    public CoCommentDTO(TotalComment coComment, TotalComment mention, Long memberId, Long authorId) {
        Long coCommentWriter = coComment.getMember().getId();

        this.commentId = coComment.getId();
        this.parentCommentId = coComment.getParentCommentId();

        if (coComment.isAnonymous() == false && !(Objects.equals(coCommentWriter, authorId))) {
            this.nickname = coComment.getMember().getNickname();
        } else if (coComment.isAnonymous() && coComment.getAnonymousId() != 0L){
            this.nickname = "익명 " + coComment.getAnonymousId();
        } else if (!coComment.isAnonymous() && Objects.equals(coCommentWriter, authorId)) {
            this.nickname = coComment.getMember().getNickname() + "(글쓴이)";
        } else if (coComment.isAnonymous() && Objects.equals(coCommentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }

        Long mentionWriter = mention.getMember().getId();

        if (mention.isAnonymous() == false) {
            this.mention = mention.getMember().getNickname();
        } else if (mention.isAnonymous() && mention.getAnonymousId() != 0L){
            this.mention = "익명 " + mention.getAnonymousId();
        } else if (!mention.isAnonymous() && Objects.equals(mentionWriter, authorId)) {
            this.mention = mention.getMember().getNickname() + "(글쓴이)";
        } else if (mention.isAnonymous() && Objects.equals(mentionWriter, authorId)) {
            this.mention = "익명(글쓴이)";
        }

        if (coComment.getStatus() == PostStatus.REPORTED) {
            this.content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (coComment.getStatus() == PostStatus.INACTIVE) {
            this.content = "삭제된 댓글입니다.";
            nickname = "(비공개됨)";
        } else {
            this.content = coComment.getContent();
        }

        this.likeCount = coComment.getTotalCommentLikes().size();


        for (TotalCommentLike tpl : coComment.getTotalCommentLikes()) {
            if (Objects.equals(tpl.getMember().getId(), memberId)) {
                this.isLiked = true;
                break;
            } else {
                this.isLiked = false;
            }
        }

        if (Objects.equals(coCommentWriter, memberId)) {
            isMyComment = true;
        }
        if (Objects.equals(coCommentWriter, authorId)){
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }
        if (coComment.getStatus() == PostStatus.INACTIVE) {
            isCommentDeleted = true;
        } else {
            isCommentDeleted = false;
        }
        if (coComment.getStatus() == PostStatus.REPORTED) {
            isCommentReported = true;
        } else {
            isCommentReported = false;
        }
        this.createdAt = convertToDateAndTime(coComment.getCreatedAt());

    }


    //univ
    public CoCommentDTO(UnivComment mention, UnivComment coComment, Long memberId, Long authorId) {
        Long coCommentWriter = coComment.getMember().getId();

        this.commentId = coComment.getId();
        this.parentCommentId = coComment.getParentCommentId();

        if (!coComment.isAnonymous() && Objects.equals(coCommentWriter, authorId)) {
            this.nickname = coComment.getMember().getNickname()+ "(글쓴이)";
        } else if (coComment.isAnonymous() && coComment.getAnonymousId() != 0L) {
            this.nickname = "익명 " + coComment.getAnonymousId();
        } else if (coComment.isAnonymous() == false) {
            this.nickname = coComment.getMember().getNickname();
        } else if (coComment.isAnonymous() && Objects.equals(coCommentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }

        Long mentionWriter = mention.getMember().getId();

        if (mention.isAnonymous() == false) {
            this.mention = mention.getMember().getNickname();
        } else if (mention.isAnonymous() && mention.getAnonymousId() != 0L){
            this.mention = "익명 " + mention.getAnonymousId();
        } else if (!mention.isAnonymous() && Objects.equals(mentionWriter, authorId)) {
            this.mention = mention.getMember().getNickname() + "(글쓴이)";
        } else if (mention.isAnonymous() && Objects.equals(mentionWriter, authorId)) {
            this.mention = "익명(글쓴이)";
        }

        if (coComment.getStatus() == PostStatus.REPORTED) {
            this.content = "신고된 댓글입니다.";
            nickname = "(비공개됨)";
        } else if (coComment.getStatus() == PostStatus.INACTIVE) {
            this.content = "삭제된 댓글입니다.";
            nickname = "(비공개됨)";
        } else {
            this.content = coComment.getContent();
        }

        this.likeCount = coComment.getUnivCommentLikes().size();

        for (UnivCommentLike ucl : coComment.getUnivCommentLikes()) { //영속성
            if (Objects.equals(ucl.getMember().getId(), memberId)) { //배치사이즈?
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }

        if (Objects.equals(coComment.getMember().getId(), memberId)) {
            isMyComment = true;
        }
        if (Objects.equals(coCommentWriter, memberId)) {
            isMyComment = true;
        }
        if (Objects.equals(coCommentWriter, authorId)){
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }
        if (coComment.getStatus() == PostStatus.INACTIVE) {
            isCommentDeleted = true;
        } else {
            isCommentDeleted = false;
        }
        if (coComment.getStatus() == PostStatus.REPORTED) {
            isCommentReported = true;
        } else {
            isCommentReported = false;
        }

        this.createdAt = convertToDateAndTime(coComment.getCreatedAt());

    }
}
