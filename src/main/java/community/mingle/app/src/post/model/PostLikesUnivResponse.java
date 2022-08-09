package community.mingle.app.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class PostLikesUnivResponse {

    private Long id;
    private int likeCount;

}
