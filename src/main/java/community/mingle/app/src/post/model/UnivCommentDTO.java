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
    private int likeCount;
    private boolean isMyComment;
    private boolean isLiked;
    private String createdTime;
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
        likeCount = c.getUnivCommentLikes().size();

        for (UnivCommentLike ucl : c.getUnivCommentLikes()) { //영속성
            if (ucl.getMember().getId() == memberId) { //배치사이즈?
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }
        if (c.getMember().getId() == memberId) {
            isMyComment = true;
        }

        createdTime = convertLocaldatetimeToTime(c.getCreatedAt());
        coCommentsList = cc;
    }
}
