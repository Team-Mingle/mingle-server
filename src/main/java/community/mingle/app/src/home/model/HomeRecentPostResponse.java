package community.mingle.app.src.home.model;

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
public class HomeRecentPostResponse {
    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private Boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdAt;


    public HomeRecentPostResponse(TotalPost totalPost) {
        this.postId = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }
        this.isFileAttached = totalPost.getIsFileAttached();
        this.likeCount = totalPost.getTotalPostLikes().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = totalPost.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());
    }

    public HomeRecentPostResponse(UnivPost p) {
        postId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        if (p.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        isFileAttached = p.getIsFileAttached();
        likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }
}
