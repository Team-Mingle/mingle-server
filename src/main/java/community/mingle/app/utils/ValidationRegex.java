package community.mingle.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    //https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/
    public static boolean isRegexPassword(String password) { //영문, 숫자 포함 6자 이상
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
//                + "(?=.*[@#$%^&+=!])"
                + "(?=\\S+$).{6,20}$";
        if (password == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isRegexChatUrl(String chatUrl) {
        String regex = "^https:\\/\\/open\\.kakao\\.com\\/.*$";
        Pattern pattern = Pattern.compile(regex);
        if (chatUrl == null) return false;
        Matcher matcher = pattern.matcher(chatUrl);
        return matcher.matches();
    }
}

