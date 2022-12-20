package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class MyPagePostDTO {
    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdAt;

    public MyPagePostDTO (TotalPost p) {
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
    }

    public MyPagePostDTO (UnivPost p) {
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
    }

}