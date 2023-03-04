package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UserStatus;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;
import static community.mingle.app.src.domain.PostStatus.DELETED;
import static community.mingle.app.src.domain.PostStatus.REPORTED;

@Getter
public class MyPagePostDTO {
    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private boolean isBlinded;
    private boolean isReported;

    private int likeCount;
    private int commentCount;
    private String createdAt;
    private boolean isAdmin;

    public MyPagePostDTO (TotalPost p, Long memberId) {
        this.postId = p.getId();
        this.title = p.getTitle();
        this.contents = p.getContent();
        if (p.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else{
            this.nickname = p.getMember().getNickname();
        }
        this.isFileAttached = p.getIsFileAttached();
        this.likeCount = p.getTotalPostLikes().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = p.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
        if (p.getTotalBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId))) {
            this.isBlinded = true;
        } else{
            this.isBlinded = false;
        }
        this.isReported = p.getStatus().equals(REPORTED) || p.getStatus().equals(DELETED); // 2/17 추가
        if (p.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.contents = "";
        }
        if (p.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 운영진에 의해 삭제된 글입니다.";
            this.contents = "";
        }
        this.isAdmin = p.getMember().getRole().equals("ADMIN");
    }

    public MyPagePostDTO (UnivPost p, Long memberId) {
        this.postId = p.getId();
        this.title = p.getTitle();
        this.contents = p.getContent();
        if (p.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else{
            this.nickname = p.getMember().getNickname();
        }
        this.isFileAttached = p.getIsFileAttached();
        this.likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
        if (p.getUnivBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId))) {
            this.isBlinded = true;
        } else{
            this.isBlinded = false;
        }
        this.isReported = p.getStatus().equals(REPORTED) || p.getStatus().equals(DELETED); // 2/17 추가
        if (p.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.contents = "";
        }
        if (p.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 운영진에 의해 삭제된 글입니다.";
            this.contents = "";
        }
        this.isAdmin = p.getMember().getRole().equals("ADMIN");
    }

}
