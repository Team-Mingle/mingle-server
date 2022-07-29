package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetTotalPostDetailResponse {

    private String title;
    private String content;
    private String nickname;
    private int likeCount;
    private int scrapCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private List<GetTotalPostDetailComments> getTotalPostDetailCommentsList;

    public GetTotalPostDetailResponse(TotalPost tp, List<GetTotalPostDetailComments> getTotalPostDetailCommentsList) {
        this.title = tp.getTitle();
        this.content = tp.getContent();
        this.nickname = tp.getMember().getNickname();
        this.likeCount = tp.getTotalPostLikes().size();
        this.scrapCount = tp.getTotalPostScraps().size();
        this.commentCount = getTotalPostDetailCommentsList.size();
        this.createdAt = tp.getCreatedAt();
        this.getTotalPostDetailCommentsList = getTotalPostDetailCommentsList;
    }
}
