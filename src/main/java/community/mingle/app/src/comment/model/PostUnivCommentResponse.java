package community.mingle.app.src.comment.model;

import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.UserRole;
import lombok.Getter;

import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostUnivCommentResponse {

    private final Long commentId;
    private String nickname;
    private final String createdAt;
    private final boolean isCommentFromAuthor;


    public PostUnivCommentResponse(Long anonymousId, UnivComment univComment, Long authorId) {
        Long commentWriter = univComment.getMember().getId();

        this.commentId = univComment.getId();
        if (!univComment.isAnonymous() && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = univComment.getMember().getNickname();
        } else if (univComment.isAnonymous() && anonymousId != 0L) {
            this.nickname = "익명 " + anonymousId;
        } else if (!univComment.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = univComment.getMember().getNickname() + "(글쓴이)";
        } else if (univComment.isAnonymous() && Objects.equals(commentWriter, authorId)) {
            this.nickname = "익명(글쓴이)";
        }
        if (univComment.getMember().getRole() == UserRole.FRESHMAN) {
            this.nickname = "🐥" + this.nickname;
        }

        isCommentFromAuthor = Objects.equals(commentWriter, authorId);
        createdAt = convertToDateAndTime(univComment.getCreatedAt());


    }
}
