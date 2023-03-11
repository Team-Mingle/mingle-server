package community.mingle.app.src.post.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifiedMemberResponse {

    public NotifiedMemberResponse(String userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    private String userId;
    private String nickname;

}
