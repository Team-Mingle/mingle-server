package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class PostUnivPostDTO {
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
    private List<UnivCommentDTO> commentList;


    //private List<PostImgDTO> postImgUrls;


    public PostUnivPostDTO(UnivPost u) {
        univPostId = u.getId();
        title = u.getTitle();
        content = u.getContent();
        if (u.getIsAnonymous() == true) {
            nickname = "글쓴이";
        } else {
            nickname = u.getMember().getNickname();
        }
        likeCount = 0;
        scrapCount = 0;
        commentCount = 0;
        this.isMyPost = true;
        this.isLiked = false;
        this.isScraped = false;
        createdTime = convertLocaldatetimeToTime(u.getCreatedAt());
        commentList = Collections.emptyList();
    }
}
