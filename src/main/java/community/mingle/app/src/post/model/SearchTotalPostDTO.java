package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class SearchTotalPostDTO {

//    private Long totalPostId;
//    private String title;
//    private String content;
//    private String nickname;
//    private String createdAt;

    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdAt;



    public SearchTotalPostDTO(TotalPost totalPost) {
        this.postId = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else{
            this.nickname = totalPost.getMember().getNickname();
        }
        this.isFileAttached = totalPost.getIsFileAttached();
        this.likeCount = totalPost.getTotalPostLikes().size();
        this.commentCount = totalPost.getTotalPostComments().size();
        this.createdAt = convertLocaldatetimeToTime(totalPost.getCreatedAt());

    }
}
