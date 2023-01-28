package fintech.evolution.utils;

import java.util.regex.Pattern;

import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;

public class Utils {

    public static Pattern pattern = Pattern.compile("\\+998\\d{9}");

    public static boolean isFormalContact(String contact) {
        return pattern.matcher(contact).matches();
    }

    public static String getTextByLang(String lang, String textIfTrue, String textIfFalse){
        return LANG_UZ.equals(lang) ? textIfTrue : textIfFalse;
    }

}

