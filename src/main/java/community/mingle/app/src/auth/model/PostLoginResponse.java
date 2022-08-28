package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class PostLoginResponse {
//    private String email;
    private Long userId;
    private String email;
    private String jwt;
    private String refreshJwt;

}
