package community.mingle.app.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum Qaers {
    정현우("lj", "정현우"),
    김태현("th", "김태현"),
    최정안("ja", "최정안"),
    김종하("jh", "김종하"),
    송의진("uj", "송의진"),
    윤민서("ms", "윤민서"),
    이현재("hj", "이현재"),
    강현우("hw", "강현우"),
    박상철("sc", "박상철"),
    김민준("mj", "김민준"),
    최보영("by", "최보영");

    private final String initial;
    private final String koreanName;
}
