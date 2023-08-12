package community.mingle.app.src.domain;

import lombok.Getter;

import java.util.List;

@Getter
public enum Currency {


    KRW(List.of("홍콩", "싱가포르", "영국")),
    HKD(List.of("홍콩")),
    SGD(List.of("싱가포르")),
    GBP(List.of("영국"));

    private final List<String> countries;

    Currency(List<String> countries) {
        this.countries = countries;


    }
}
