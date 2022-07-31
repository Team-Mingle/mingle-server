package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class TotalPostDto {

    private String title;
    private String content;
    private String nickname;
    private int likeCount;
    private int scrapCount;
    private String createdAt;

    public TotalPostDto(TotalPost totalPost) {
        this.title = totalPost.getTitle();
        this.content = totalPost.getContent();
        if (totalPost.isAnonymous() == true) {
            this.nickname = "글쓴이";
        } else{
            this.nickname = totalPost.getMember().getNickname();
        }
        this.likeCount = totalPost.getTotalPostLikes().size();
        this.scrapCount = totalPost.getTotalPostScraps().size();
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());
    }
}
