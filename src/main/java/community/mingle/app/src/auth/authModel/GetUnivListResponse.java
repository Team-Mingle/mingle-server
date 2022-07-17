package community.mingle.app.src.auth.authModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1.1 학교 리스트 보내주기
 */
@Getter
@AllArgsConstructor
public class GetUnivListResponse {
    private int id;
    private String name;
}
