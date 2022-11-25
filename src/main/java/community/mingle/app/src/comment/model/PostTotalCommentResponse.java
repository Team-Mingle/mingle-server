package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Total.TotalComment;
import lombok.Getter;

import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostTotalCommentResponse {
    Long commentId;
    String nickname;
    String createdAt;
    private boolean isCommentFromAuthor;

    public PostTotalCommentResponse(Long anonymousId, TotalComment totalComment, Long authorId) {
        Long commentWriter = totalComment.getMember().getId();
        this.commentId = totalComment.getId();
        if (totalComment.isAnonymous() == false) {
            this.nickname = totalComment.getMember().getNickname();
        } else if (totalComment.isAnonymous() && anonymousId != 0){
            this.nickname = "익명 " + anonymousId;
        } else if (totalComment.isAnonymous() == false && totalComment.getMember().getId() == authorId) {
            this.nickname = totalComment.getMember().getNickname() + "(글쓴이)";
        } else {
            this.nickname = "익명(글쓴이)";
        }

        if (Objects.equals(commentWriter, authorId)) {
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }
        createdAt = convertToDateAndTime(totalComment.getCreatedAt());


    }
}
