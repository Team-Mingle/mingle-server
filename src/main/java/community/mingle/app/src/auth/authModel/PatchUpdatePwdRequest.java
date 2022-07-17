package community.mingle.app.src.auth.authModel;

import lombok.Getter;

@Getter
public class PatchUpdatePwdRequest {
    private String email;
    private String pwd;
    private String rePwd;
}
