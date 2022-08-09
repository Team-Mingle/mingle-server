package community.mingle.app.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class PostCommentLikesUnivResponse {

    private Long id;
    private int likeCount;

}
