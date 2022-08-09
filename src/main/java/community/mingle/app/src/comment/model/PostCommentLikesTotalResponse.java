package community.mingle.app.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class PostCommentLikesTotalResponse {

    private Long id;
    private int likeCount;

}
