package community.mingle.app.src.auth.authModel;

import lombok.Getter;

@Getter
public class PostSignupRequest {
    private String email;
    private String pwd;
    private String nickname;
    private String isAgreed;
}
