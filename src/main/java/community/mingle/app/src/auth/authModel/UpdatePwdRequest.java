package community.mingle.app.src.auth.authModel;

import lombok.Getter;

@Getter
public class UpdatePwdRequest {
    private String email;
    private String pwd;
}
