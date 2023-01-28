package fintech.evolution.utils;

import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;

public class Utils {

    public static String getTextByLang(String lang, String textIfTrue, String textIfFalse){
        return LANG_UZ.equals(lang) ? textIfTrue : textIfFalse;
    }

}

