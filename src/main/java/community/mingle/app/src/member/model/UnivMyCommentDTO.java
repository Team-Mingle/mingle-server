package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class UnivMyCommentDTO {
    private Long univPostId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private String createdAt;

    public UnivMyCommentDTO(UnivPost p) {
        this.univPostId = p.getId();
        this.title = p.getTitle();
        this.contents = p.getContent();
        if (p.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else{
            this.nickname = p.getMember().getNickname();
        }
        this.likeCount = p.getUnivPostLikes().size();
        this.commentCount = p.getUnivComments().size();
        this.createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }
}
