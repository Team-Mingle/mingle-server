package community.mingle.app.src.auth.authModel;

import lombok.Getter;

@Getter
public class PostLoginRequest {
    private String email;
    private String pwd;
}
