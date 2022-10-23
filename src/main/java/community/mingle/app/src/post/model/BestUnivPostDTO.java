package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
//@AllArgsConstructor
public class BestUnivPostDTO {

    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int commentCount;
    private String createdAt;
//    private String postImgUrl;


    public BestUnivPostDTO(UnivPost p) {
        postId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        if (p.getIsAnonymous() == true) {
            this.nickname = "글쓴이";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        this.isFileAttached = p.getIsFileAttached();
        likeCount = p.getUnivPostLikes().size();
        commentCount = p.getUnivComments().size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
