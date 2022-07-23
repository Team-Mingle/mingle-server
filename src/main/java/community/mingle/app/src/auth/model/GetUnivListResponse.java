package community.mingle.app.src.auth.model;

import community.mingle.app.src.domain.UnivName;
import lombok.Getter;

/**
 * 1.1 학교 리스트 보내주기
 */
@Getter
public class GetUnivListResponse {
    private int univIdx;
    private String name;

    public GetUnivListResponse(UnivName univName) {
        this.univIdx = univName.getId();
        this.name = univName.getUnivName();
    }
}