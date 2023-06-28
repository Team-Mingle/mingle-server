package community.mingle.app.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeConverter {
    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        String msg = null;
        if (diffTime < 60) {
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

    public static LocalDateTime convertStringToLocalDateTime(String dateString) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime convertedDateTime = null;

        if (dateString.endsWith("초전")) {
            int diffTime = Integer.parseInt(dateString.replace("초전", ""));
            convertedDateTime = now.minusSeconds(diffTime);
        } else if (dateString.endsWith("분 전")) {
            int diffTime = Integer.parseInt(dateString.replace("분 전", ""));
            convertedDateTime = now.minusMinutes(diffTime);
        } else if (dateString.endsWith("시간 전")) {
            int diffTime = Integer.parseInt(dateString.replace("시간 전", ""));
            convertedDateTime = now.minusHours(diffTime);
        } else if (dateString.endsWith("일 전")) {
            int diffTime = Integer.parseInt(dateString.replace("일 전", ""));
            convertedDateTime = now.minusDays(diffTime);
        } else if (dateString.endsWith("개월 전")) {
            int diffTime = Integer.parseInt(dateString.replace("개월 전", ""));
            convertedDateTime = now.minusMonths(diffTime);
        } else if (dateString.endsWith("년 전")) {
            int diffTime = Integer.parseInt(dateString.replace("년 전", ""));
            convertedDateTime = now.minusYears(diffTime);
        }

        return convertedDateTime;
    }


    public static String convertToDateAndTime(LocalDateTime localDateTime) {
        String dateFormat = localDateTime.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        return dateFormat;
    }


}
