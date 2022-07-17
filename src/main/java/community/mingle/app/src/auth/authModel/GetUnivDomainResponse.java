package community.mingle.app.src.auth.authModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 학교 도메인 보내주기
 */
@Getter
@AllArgsConstructor
public class GetUnivDomainResponse {
    private int id;
    private String domain;
    //private List<UnivEmail> univEmailList ;
}
