package community.mingle.app.src.post.model;
import community.mingle.app.src.domain.Univ.UnivComment;
import lombok.Getter;

import java.util.List;
import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class UnivCommentDTO {

    private Long commentId;
    private String nickname;
    private String content;
    private String createdTime;
    private int likeCount;
    private List<UnivCoCommentDTO> coCommentsList;


    public UnivCommentDTO (UnivComment c, List<UnivCoCommentDTO> cc) {
        commentId = c.getId();
        nickname = c.getMember().getNickname();
        content = c.getContent();
        createdTime = convertLocaldatetimeToTime(c.getCreatedAt());
        likeCount = c.getUnivCommentLikes().size();
        coCommentsList = cc;
    }
}
