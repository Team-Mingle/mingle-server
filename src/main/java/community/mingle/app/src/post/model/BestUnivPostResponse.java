package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
//@AllArgsConstructor
public class BestUnivPostResponse {

    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
//    private LocalDateTime createdTime;
    private String createdAt;


    public BestUnivPostResponse(UnivPost p) {
        postId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        if (p.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        likeCount = p.getUnivPostLikes().size();
        commentCount = p.getUnivComments().size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
