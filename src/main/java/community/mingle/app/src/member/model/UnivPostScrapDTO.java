package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
@AllArgsConstructor
public class UnivPostScrapDTO {

    private Long univPostId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private String createdTime;


    public UnivPostScrapDTO(UnivPost p) {
        univPostId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        likeCount = p.getUnivPostLikes().size();
        commentCount = p.getUnivComments().size();
        createdTime = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
