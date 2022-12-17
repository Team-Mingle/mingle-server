package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

/**
 * 얘 안쓰이는데 어디?
 * 나중에 합치기 10/23
 */
@Getter
public class PostListDTO {
    private Long totalPostIdx;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdTime;
    private String postImgUrl;


    public PostListDTO(TotalPost totalPost) {
        this.totalPostIdx = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        this.nickname = totalPost.getMember().getNickname();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }
        this.likeCount = totalPost.getTotalPostLikes().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = totalPost.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdTime = convertLocaldatetimeToTime(totalPost.getCreatedAt());
        if(totalPost.getIsFileAttached() == true) {
            this.postImgUrl = totalPost.getTotalPostImages().get(0).getImgUrl();
        }
    }


    public PostListDTO(UnivPost univPost) {
        this.totalPostIdx = univPost.getId();
        this.title = univPost.getTitle();
        this.contents = univPost.getContent();
        this.nickname = univPost.getMember().getNickname();
        if (univPost.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = univPost.getMember().getNickname();
        }
        this.isFileAttached = univPost.getIsFileAttached();
        this.likeCount = univPost.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = univPost.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdTime = convertLocaldatetimeToTime(univPost.getCreatedAt());
        if(univPost.getIsFileAttached() == true) {
            this.postImgUrl = univPost.getUnivPostImages().get(0).getImgUrl();
        }
    }
}
