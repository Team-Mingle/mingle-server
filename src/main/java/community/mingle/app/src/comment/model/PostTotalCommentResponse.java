package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.UserRole;
import lombok.Getter;

import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostTotalCommentResponse {
    Long commentId;
    String nickname;
    String createdAt;
    private final boolean isCommentFromAuthor;

    public PostTotalCommentResponse(Long anonymousId, TotalComment totalComment, Long authorId) {
        Long commentWriter = totalComment.getMember().getId();

        this.commentId = totalComment.getId();
        if (!totalComment.isAnonymous() && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = totalComment.getMember().getNickname();
        } else if (totalComment.isAnonymous() && anonymousId != 0L) {
            this.nickname = "익명 " + anonymousId;
        } else if (!totalComment.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = totalComment.getMember().getNickname() + "(글쓴이)";
        } else if ((totalComment.isAnonymous() && Objects.equals(commentWriter, authorId))) {
            this.nickname = "익명(글쓴이)";
        }

        if (totalComment.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }

        isCommentFromAuthor = Objects.equals(commentWriter, authorId);
        createdAt = convertToDateAndTime(totalComment.getCreatedAt());


    }
}
