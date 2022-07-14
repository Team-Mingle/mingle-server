package community.mingle.app.src.auth.authModel;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 1.4.1 인증코드 전송 API
 */
@Data
public class PostEmailRequest {
//    @Email
    @NotBlank(message = "이메일(필수)")
    private String email;

}
