package community.mingle.app.src.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemCommentLikeResponse {
    private Long id;
    private int likeCount;
}
