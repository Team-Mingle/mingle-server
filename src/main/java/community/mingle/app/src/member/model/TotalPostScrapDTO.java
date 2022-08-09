package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
@AllArgsConstructor
public class TotalPostScrapDTO {

    private Long totalPostId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private String createdTime;


    public TotalPostScrapDTO(TotalPost p) {
        totalPostId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        likeCount = p.getTotalPostLikes().size();
        commentCount = p.getTotalPostComments().size();
        createdTime = convertLocaldatetimeToTime(p.getCreatedAt());
    }
}
