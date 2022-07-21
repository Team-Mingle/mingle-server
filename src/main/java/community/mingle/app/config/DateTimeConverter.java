package community.mingle.app.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeConverter {
    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        String msg = null;
        if (diffTime < 60){
            return diffTime + "초전";
        }
        diffTime = diffTime / 60;
        if (diffTime < 60) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / 60;
        if (diffTime < 24) {
            return diffTime + "시간 전";
        }
        diffTime = diffTime / 24;
        if (diffTime < 30) {
            return diffTime + "일 전";
        }
        diffTime = diffTime / 30;
        if (diffTime < 12) {
            return diffTime + "개월 전";
        }

        diffTime = diffTime / 12;
        return diffTime + "년 전";
    }

}
