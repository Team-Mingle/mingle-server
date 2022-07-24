package community.mingle.app.src.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUpdatePwdRequest {
//    private Long userIdx;
    private String email;
    private String pwd;
    private String rePwd;
}
