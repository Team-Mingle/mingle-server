package community.mingle.app.src.home.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import community.mingle.app.src.domain.BoardType;
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

@Getter
public class HomePostResponse {

    private final Long postId;
    private final String title;
    private final String contents;
    private final boolean isFileAttached;
    private final boolean isBlinded;
    private final boolean isAdmin;
    private final int likeCount;
    private final int commentCount;
    private final String createdAt;
    private final BoardType boardType;
    private String nickname;


    public HomePostResponse(TotalPost totalPost, Long memberId) {
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
        this.isBlinded = totalPost.getTotalBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId));
        this.isAdmin = totalPost.getMember().getRole().equals(UserRole.ADMIN);
        this.likeCount = totalPost.getTotalPostLikes().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = totalPost.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());
        this.boardType = BoardType.광장;
    }

    public HomePostResponse(UnivPost p, Long memberId) {
        postId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        if (p.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        if (p.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }
        isFileAttached = p.getIsFileAttached();
        this.isBlinded = p.getUnivBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId));
        this.isAdmin = p.getMember().getRole().equals(UserRole.ADMIN);
        likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
        this.boardType = BoardType.잔디밭;
    }

    @JsonIgnore
    public LocalDateTime getCreatedAtDateTime() {
        return convertStringToLocalDateTime(createdAt);
    }


}
