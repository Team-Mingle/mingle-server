package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class UnivPostDTO {

    private Long univPostId;
    private String title;
    private String content;
    private String nickname;
    private int likeCount;
    private int scrapCount;
    private int commentCount;
    private boolean isMyPost;
    private boolean isLiked;
    private boolean isScraped;
    private String createdTime;

    //private List<PostImgDTO> postImgUrls;


    public UnivPostDTO(UnivPost u, boolean isMyPost, boolean isLiked, boolean isScraped) {
        univPostId = u.getId();
        title = u.getTitle();
        content = u.getContent();
        if (u.isAnonymous() == true) {
            this.nickname = "글쓴이";
        } else {
            this.nickname = u.getMember().getNickname();
        }
        likeCount = u.getUnivPostLikes().size();
        scrapCount = u.getUnivPostScraps().size();
        commentCount = u.getUnivComments().size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isScraped = isScraped;
        createdTime = convertLocaldatetimeToTime(u.getCreatedAt());
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isScraped = isScraped;
    }
}
