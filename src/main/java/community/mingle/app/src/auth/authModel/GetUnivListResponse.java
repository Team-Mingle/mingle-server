package community.mingle.app.src.auth.authModel;

import community.mingle.app.src.domain.UnivName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1.1 학교 리스트 보내주기
 */
@Getter
@AllArgsConstructor
public class GetUnivListResponse {
    private int univIdx;
    private UnivName univ;
}
