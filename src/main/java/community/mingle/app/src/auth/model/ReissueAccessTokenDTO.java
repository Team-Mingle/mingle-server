package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueAccessTokenDTO {
    private String accessToken;
    private String refreshToken;

}
