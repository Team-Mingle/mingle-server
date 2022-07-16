package community.mingle.app.src.auth.authModel;

import lombok.Getter;

@Getter
public class PostSignupResponse {
    private String jwt;
    private int member_id;
}
