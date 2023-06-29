package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UserRole;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;
import static community.mingle.app.src.domain.PostStatus.DELETED;
import static community.mingle.app.src.domain.PostStatus.REPORTED;

@Getter
public class MyPagePostDTO {
    private final Long postId;
    private String title;
    private String contents;
    private String nickname;
    private final boolean isFileAttached;
    private final boolean isBlinded;
    private final boolean isReported;

    private final int likeCount;
    private final int commentCount;
    private final String createdAt;
    private final boolean isAdmin;

    public MyPagePostDTO(TotalPost totalPost, Long memberId) {
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
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());
        this.isBlinded = totalPost.getTotalBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId));
        this.isReported = totalPost.getStatus().equals(REPORTED) || totalPost.getStatus().equals(DELETED); // 2/17 추가
        if (totalPost.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.contents = "";
        }
        if (totalPost.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.contents = "";
        }
        this.isAdmin = totalPost.getMember().getRole().equals(UserRole.ADMIN);
    }

    public MyPagePostDTO(UnivPost p, Long memberId) {
        this.postId = p.getId();
        this.title = p.getTitle();
        this.contents = p.getContent();
        if (p.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        this.isFileAttached = p.getIsFileAttached();
        this.likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
        this.isBlinded = p.getUnivBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId));
        this.isReported = p.getStatus().equals(REPORTED) || p.getStatus().equals(DELETED); // 2/17 추가
        if (p.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.contents = "";
        }
        if (p.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.contents = "";
        }
        this.isAdmin = p.getMember().getRole().equals(UserRole.ADMIN);
    }

}
