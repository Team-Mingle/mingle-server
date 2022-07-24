package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 학교 도메인 보내주기
 */
@Getter
@AllArgsConstructor
public class GetUnivDomainResponse {
    private int emailIdx;
    private String domain;

}