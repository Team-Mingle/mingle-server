package community.mingle.app.src.post.model;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivCommentLike;
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
    private boolean isLiked;
    private List<UnivCoCommentDTO> coCommentsList;


    public UnivCommentDTO (UnivComment c, List<UnivCoCommentDTO> cc, Long memberId) {
        commentId = c.getId();
//        nickname = c.getMember().getNickname();
        if (c.isAnonymous() == true) {
            this.nickname = "익명 "+c.getAnonymousId();
        } else {
            this.nickname = c.getMember().getNickname();
        }
        content = c.getContent();
        createdTime = convertLocaldatetimeToTime(c.getCreatedAt());
        likeCount = c.getUnivCommentLikes().size();
        coCommentsList = cc;

        for (UnivCommentLike ucl : c.getUnivCommentLikes()) { //영속성
            if (ucl.getMember().getId() == memberId) { //배치사이즈?
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }
    }
}
