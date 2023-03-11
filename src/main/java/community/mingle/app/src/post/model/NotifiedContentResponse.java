package community.mingle.app.src.post.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifiedContentResponse {
    public NotifiedContentResponse(String userId, String nickname, String contentId, String title) {
        this.userId = userId;
        this.nickname = nickname;
        this.contentId = contentId;
        this.title = title;
    }

    private String userId;
    private String nickname;
    private String contentId;
    private String title;
}
