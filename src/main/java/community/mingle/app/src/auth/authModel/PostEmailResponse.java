package community.mingle.app.src.auth.authModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1.4.1 인증코드 전송 API
 */
@Getter
@AllArgsConstructor
public class PostEmailResponse {
    private String message;
}
