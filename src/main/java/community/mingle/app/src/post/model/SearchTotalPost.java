package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class SearchTotalPost {

    private Long totalPostId;
    private String title;
    private String content;
    private String nickname;
    private String createdAt;



    public SearchTotalPost(TotalPost totalPost) {
        this.totalPostId = totalPost.getId();
        this.title = totalPost.getTitle();
        this.content = totalPost.getContent();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else{
            this.nickname = totalPost.getMember().getNickname();
        }
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());

    }
}
