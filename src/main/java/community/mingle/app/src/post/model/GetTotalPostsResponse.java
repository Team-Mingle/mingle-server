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
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdTime;

    private String postImgUrl;


    public GetTotalPostsResponse(TotalPost totalPost) {
        this.totalPostIdx = totalPost.getId();
        this.title = totalPost.getTitle();
        this.contents = totalPost.getContent();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }

        this.isFileAttached = totalPost.getIsFileAttached();
        this.likeCount = totalPost.getTotalPostLikes().size();
        this.commentCount = totalPost.getTotalPostComments().size();
        this.createdTime = convertLocaldatetimeToTime(totalPost.getCreatedAt());

        if(totalPost.getIsFileAttached() == true) {
            this.postImgUrl = totalPost.getTotalPostImages().get(0).getImgUrl(); //없는데 true여서 가져오려햇는데 어레이리스트 0번째가 없어서 인덱스에러남 이래서 디비에 막넣으면안됨 ;;
        }
    }

}
