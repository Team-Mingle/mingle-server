package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.ItemComment;
import community.mingle.app.src.domain.Total.TotalComment;
import lombok.Getter;

import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostItemCommentResponse {

    Long commentId;
    String nickname;
    String createdAt;

    private boolean isCommentFromAuthor;

    public PostItemCommentResponse(Long anonymousId, ItemComment itemComment, Long authorId) {
        Long commentWriter = itemComment.getMember().getId();

        this.commentId = itemComment.getId();
        if (itemComment.isAnonymous() == false && !(Objects.equals(commentWriter, authorId))) {
            this.nickname = itemComment.getMember().getNickname();
        } else if (itemComment.isAnonymous() && anonymousId != 0L){
            this.nickname = "익명 " + anonymousId;
        } else if (itemComment.isAnonymous() == false && Objects.equals(commentWriter, authorId)) {
            this.nickname = itemComment.getMember().getNickname() + "(글쓴이)";
        } else if ((itemComment.isAnonymous() && Objects.equals(commentWriter, authorId))){
            this.nickname = "익명(글쓴이)";
        }

        if (Objects.equals(commentWriter, authorId)) {
            isCommentFromAuthor = true;
        } else {
            isCommentFromAuthor = false;
        }
        createdAt = convertToDateAndTime(itemComment.getCreatedAt());


    }
}
