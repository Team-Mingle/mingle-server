package community.mingle.app.src.auth.authModel;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 이메일 받기
 */
@Data
public class PostUserEmailRequest {
    private String email;
}
