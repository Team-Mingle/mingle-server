package community.mingle.app.src.auth.authModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLoginRequest {
    private String email;
    private String pwd;
}
