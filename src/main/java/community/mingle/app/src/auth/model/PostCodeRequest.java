package community.mingle.app.src.auth.model;

import lombok.Getter;

/**
 * 1.4.2 인증 코드 검사 API
 */
@Getter
public class PostCodeRequest {

    private String email;
    private String code;
}
