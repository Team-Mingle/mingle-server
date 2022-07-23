package community.mingle.app.src.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLoginRequest {
    private String email;
    private String pwd;
}
