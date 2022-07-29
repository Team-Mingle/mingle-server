package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetTotalPostDetailComments {
    private Long commentId;
    private List<GetTotalPostDetailCocomment> getTotalPostDetailCocomments;
    private String content;
    private String nickname;
    private int likeCount;
    private LocalDateTime createdAt;


    public GetTotalPostDetailComments(TotalComment tc, List<GetTotalPostDetailCocomment> getTotalPostDetailCocomment) {
        this.commentId = tc.getId();
        this.getTotalPostDetailCocomments = getTotalPostDetailCocomment;
        this.content = tc.getContent();
        this.nickname = tc.getMember().getNickname();
        this.likeCount = tc.getTotalCommentLikes().size();
        this.createdAt = tc.getCreatedAt();

    }
}
