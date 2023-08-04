package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class PostSignupRequest {
    private int univId;
    private String email;
    private String pwd;
    private String nickname;
}
