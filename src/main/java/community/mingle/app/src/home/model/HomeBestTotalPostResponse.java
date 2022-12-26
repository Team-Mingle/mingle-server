package community.mingle.app.src.home.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class HomeBestTotalPostResponse {
    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private boolean isBlinded;
    private int likeCount;
    private int commentCount;
    private String createdAt;


    public HomeBestTotalPostResponse(TotalPost totalPost, Long memberId) {
        this.postId = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }
        this.isFileAttached = totalPost.getIsFileAttached();
        if (totalPost.getTotalBlinds().stream().anyMatch(bm -> bm.getMember().getId() == memberId)) {
            this.isBlinded = true;
        }else{
            this.isBlinded = false;
        }
        this.likeCount = totalPost.getTotalPostLikes().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = totalPost.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());
    }

}
