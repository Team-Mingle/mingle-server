package community.mingle.app.src.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum Qaqa {
    HKU(1, "홍콩대", "hku"),
    HKUST(2, "과기대", "hkust"),
    CUHK(3, "중문대", "cuhk"),
    CITYU(4, "시티대", "cityu"),
    POLYU(5, "폴리대", "polyu"),
    NUS(7, "NUS", "nus"),
    NTU(8, "NTU", "ntu");

    private final int univId;
    private final String univKoreanName;
    private final String initial;
}
