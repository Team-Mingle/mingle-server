package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class GetUnivPostsResponse {

    private Long univPostIdx;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdTime;

    private String univImgUrl;


    public GetUnivPostsResponse(UnivPost univPost) {
        this.univPostIdx = univPost.getId();
        this.title = univPost.getTitle();
        this.contents = univPost.getContent();
        if (univPost.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else {
            this.nickname = univPost.getMember().getNickname();
        }
        this.isFileAttached = univPost.getIsFileAttached();
        this.likeCount = univPost.getUnivPostLikes().size();
        this.commentCount = univPost.getUnivComments().size();
        this.createdTime = convertLocaldatetimeToTime(univPost.getCreatedAt());

        if(univPost.getIsFileAttached() == true) {
            this.univImgUrl = univPost.getUnivPostImages().get(0).getImgUrl();
        }
    }

}
