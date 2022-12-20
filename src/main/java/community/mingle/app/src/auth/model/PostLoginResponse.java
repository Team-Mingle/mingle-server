package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class PostLoginResponse {
//    private String email;
    private Long memberId;
    private String email;
    private String nickName;
    private String univName;
    private String accessToken;
    private String refreshToken;

}
