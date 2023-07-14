package community.mingle.app.src.post.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.CategoryType;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;
import static community.mingle.app.config.DateTimeConverter.convertStringToLocalDateTime;
import static community.mingle.app.src.domain.PostStatus.DELETED;
import static community.mingle.app.src.domain.PostStatus.REPORTED;

@Getter
public class PostListDTO {

    private final Long postId;
    private final boolean isFileAttached;
    private final boolean isBlinded;
    private final boolean isReported;
    private final int likeCount;
    private final int commentCount;
    private final String createdAt;
    private final boolean isAdmin;
    private final BoardType boardType;
    private final CategoryType categoryType;
    private String title;
    private String contents;
    private String nickname;


    public PostListDTO(TotalPost totalPost, Long memberId) {
        this.postId = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        if (totalPost.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }
        if (totalPost.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }
        this.isFileAttached = totalPost.getIsFileAttached();
        this.likeCount = totalPost.getTotalPostLikes().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = totalPost.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.isBlinded = totalPost.getTotalBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId));
//        this.isReported = false; // 2/17 추가
        this.isReported = totalPost.getStatus().equals(REPORTED) || totalPost.getStatus().equals(DELETED); // 2/17 추가
        if (totalPost.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.contents = "";
        }
        if (totalPost.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.contents = "";
        }
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());
        this.isAdmin = totalPost.getMember().getRole().equals(UserRole.ADMIN);
        this.boardType = BoardType.광장;
        this.categoryType = CategoryType.valueOf(totalPost.getCategory().getName());
    }


    /**
     * 3.5 대학 게시물 리스트 w Report (w/o reason)
     *
     * @param univPost
     * @param memberId
     */
    public PostListDTO(UnivPost univPost, Long memberId) {
        this.postId = univPost.getId();
        this.title = univPost.getTitle();
        this.contents = univPost.getContent();
        if (univPost.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = univPost.getMember().getNickname();
        }
        if (univPost.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }
        this.isFileAttached = univPost.getIsFileAttached();
        this.likeCount = univPost.getUnivPostLikes().size();
        this.isBlinded = univPost.getUnivBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId));
        /** 댓글 개수*/
        List<UnivComment> commentList = univPost.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
//        this.isReported = false; // 2/17 추가
        this.isReported = univPost.getStatus().equals(REPORTED) || univPost.getStatus().equals(DELETED); // 2/17 추가
        if (univPost.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.contents = "";
        }
        if (univPost.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.contents = "";
        }
        this.createdAt = convertLocaldatetimeToTime(univPost.getCreatedAt());
        this.isAdmin = univPost.getMember().getRole().equals(UserRole.ADMIN);
        this.boardType = BoardType.잔디밭;
        this.categoryType = CategoryType.valueOf(univPost.getCategory().getName());
    }

    @JsonIgnore
    public LocalDateTime getCreatedAtDateTime() {
        return convertStringToLocalDateTime(createdAt);
    }

}
