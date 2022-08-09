package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class PostLikesTotalResponse {

    private Long id;
    private int likeCount;

}
