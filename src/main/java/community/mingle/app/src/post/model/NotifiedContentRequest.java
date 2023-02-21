package community.mingle.app.src.post.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifiedContentRequest {
    private Long userId;
    private String nickname;
    private Long contentId;
    private String contentType;
}
