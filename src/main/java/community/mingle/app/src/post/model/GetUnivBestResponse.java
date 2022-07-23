package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
//@AllArgsConstructor
public class GetUnivBestResponse {

    private Long univPostIdx;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
//    private LocalDateTime createdTime;
    private String createdAt;


    public GetUnivBestResponse(UnivPost p) {
        univPostIdx = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        likeCount = p.getUnivPostLikes().size();
        commentCount = p.getComments().size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
