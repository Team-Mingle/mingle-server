package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Univ.UnivComment;
import lombok.Getter;

import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostUnivCommentResponse {

    private Long commentId;
    private String nickname;
    private String createdAt;
    private boolean isCommentFromAuthor;



    public PostUnivCommentResponse(Long anonymousId, UnivComment univComment, Long authorId) {
        Long commentWriter = univComment.getMember().getId();

        this.commentId = univComment.getId();
        if (univComment.isAnonymous() == false && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = univComment.getMember().getNickname();
        } else if (univComment.isAnonymous() && anonymousId != 0L){
            this.nickname = "익명 " + anonymousId;
        } else if (!univComment.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = univComment.getMember().getNickname() + "(글쓴이)";
        } else if (univComment.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }


        if (Objects.equals(commentWriter, authorId)) {
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }
        createdAt = convertToDateAndTime(univComment.getCreatedAt());


    }
}
