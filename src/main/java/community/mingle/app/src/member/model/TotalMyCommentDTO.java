package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class TotalMyCommentDTO {
    private Long totalPostId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private String createdAt;

    public TotalMyCommentDTO(TotalPost p) {
        this.totalPostId = p.getId();
        this.title = p.getTitle();
        this.contents = p.getContent();
        if (p.isAnonymous() == true) {
            this.nickname = "익명";
        } else{
            this.nickname = p.getMember().getNickname();
        }
        this.likeCount = p.getTotalPostLikes().size();
        this.commentCount = p.getTotalPostComments().size();
        this.createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }
}
