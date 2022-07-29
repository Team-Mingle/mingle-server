package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalComment;

import java.time.LocalDateTime;

public class GetTotalPostDetailCocomment {
    private Long parentCommentId;
    private String mentioned;
    private String content;
    private String nickname;
    private int likeCount;
    private LocalDateTime createdAt;

    public GetTotalPostDetailCocomment(TotalComment tc, String mentioned) {
        this.parentCommentId = tc.getParentCommentId();
        this.mentioned = mentioned;
        this.content = tc.getContent();
        this.nickname = tc.getMember().getNickname();
        this.likeCount = tc.getTotalCommentLikes().size();
        this.createdAt = tc.getCreatedAt();
    }
}
