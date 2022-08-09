package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class GetTotalPostsResponse {
    private Long totalPostIdx;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private String createdTime;

    private String postImgUrl;


    public GetTotalPostsResponse(TotalPost totalPost) {
        this.totalPostIdx = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        this.nickname = totalPost.getMember().getNickname();
        this.likeCount = totalPost.getTotalPostLikes().size();
        this.commentCount = totalPost.getTotalPostComments().size();
        this.createdTime = convertLocaldatetimeToTime(totalPost.getCreatedAt());
//        this.postImgUrl = totalPost.getTotalPostComments().get(0);
    }

}
