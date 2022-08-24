package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class SearchUnivPost {

    private Long univPostId;
    private String title;
    private String content;
    private String nickname;
    private String createdAt;



    public SearchUnivPost(UnivPost univPost) {
        this.univPostId = univPost.getId();
        this.title = univPost.getTitle();
        this.content = univPost.getContent();
        if (univPost.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else{
            this.nickname = univPost.getMember().getNickname();
        }
        this.createdAt = convertLocaldatetimeToTime(univPost.getCreatedAt());

    }
}
