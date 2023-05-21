package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FreshmanRegisterResponse {
    private String email;
    private String code;
}
