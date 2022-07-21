package community.mingle.app.src.auth.model;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class PostSignupRequest {
    private int univId;
    private String email;
    private String pwd;
    private String nickname;
}
