package community.mingle.app.src.auth.model;

import lombok.Getter;

/**
 * 1.5 비밀번호 검증 api
 */
@Getter
public class PostPwdRequest {
    private String pwd;
    private String rePwd;
}
