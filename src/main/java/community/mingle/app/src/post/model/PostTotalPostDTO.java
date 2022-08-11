package community.mingle.app.src.post.model;
import community.mingle.app.src.domain.Total.TotalPost;

import java.util.Collections;
import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

public class PostTotalPostDTO {

    private Long totalPostId;
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
    private List<TotalCommentDto> commentList;

    //private List<PostImgDTO> postImgUrls;


    public PostTotalPostDTO(TotalPost u) {
        totalPostId = u.getId();
        title = u.getTitle();
        content = u.getContent();
        if (u.isAnonymous() == true) {
            this.nickname = "글쓴이";
        } else {
            this.nickname = u.getMember().getNickname();
        }
        likeCount = 0;
        scrapCount = 0;
        commentCount = 0;
        this.isMyPost = true;
        this.isLiked = false;
        this.isScraped = false;
        createdTime = convertLocaldatetimeToTime(u.getCreatedAt());
        commentList = Collections.emptyList();;
    }

}
