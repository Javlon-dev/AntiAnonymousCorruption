package fintech.evolution.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import fintech.evolution.repository.SecondUserDebateRepository;
import fintech.evolution.repository.UserDebateRepository;
import fintech.evolution.repository.UserReviewRepository;
import fintech.evolution.service.DocumentService;
import fintech.evolution.service.keyboard.InlineKeyboardService;
import fintech.evolution.service.keyboard.ReplyKeyboardService;
import fintech.evolution.service.sender.ExecutorService;
import fintech.evolution.variable.constants.user.UserMenu;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import static fintech.evolution.variable.constants.region.AndijanConstants.*;
import static fintech.evolution.variable.constants.region.BuxoroConstants.*;
import static fintech.evolution.variable.constants.region.FerganaConstants.*;
import static fintech.evolution.variable.constants.region.JizzakhConstants.*;
import static fintech.evolution.variable.constants.region.KarakalpakistanConstants.*;
import static fintech.evolution.variable.constants.region.KashkadaryaConstants.*;
import static fintech.evolution.variable.constants.region.KhorezmConstants.*;
import static fintech.evolution.variable.constants.region.NamanganConstants.*;
import static fintech.evolution.variable.constants.region.NavoiyConstants.*;
import static fintech.evolution.variable.constants.region.RegionConstant.*;
import static fintech.evolution.variable.constants.region.SamarkandConstants.*;
import static fintech.evolution.variable.constants.region.SirdaryaConstants.*;
import static fintech.evolution.variable.constants.region.SurkhandaryaConstants.*;
import static fintech.evolution.variable.constants.region.TashkentConstants.*;
import static fintech.evolution.variable.constants.region.TashkentRegionConstants.*;
import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;
import static fintech.evolution.variable.constants.user.UserMenu.MENU_CANCEL_RU;
import static fintech.evolution.variable.constants.user.UserMenu.MENU_CANCEL_UZ;

@Slf4j
abstract public class AbstractService {

    @Autowired
    protected UserService service;

    @Autowired
    protected InlineKeyboardService inline;

    @Autowired
    protected DocumentService documentService;

    @Autowired
    protected ReplyKeyboardService reply;

    @Autowired
    protected ExecutorService execute;

    @Autowired
    protected UserReviewRepository userReviewRepository;

    @Autowired
    protected UserDebateRepository userDebateRepository;

    @Autowired
    protected SecondUserDebateRepository secondUserDebateRepository;


    protected ReplyKeyboardMarkup getKeyboardWithRequestContact(Long chatId, String text) {
        String lang = service.getLang(chatId);
        KeyboardButton b2 = reply.getButton(lang.equals(LANG_UZ) ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        KeyboardRow r2 = reply.getRows(b2);
        if (text != null) {
            KeyboardButton b1 = reply.getRequestContactButton(text);
            KeyboardRow r1 = reply.getRows(b1);
            return reply.getKeyboard(r1, r2);
        }
        return reply.getKeyboard(r2);
    }

    protected ReplyKeyboardMarkup getKeyboardWithCancel(Long chatId, String text) {
        String lang = service.getLang(chatId);
        KeyboardButton b2 = reply.getButton(lang.equals(LANG_UZ) ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        KeyboardRow r2 = reply.getRows(b2);
        if (text != null) {
            KeyboardButton b1 = reply.getButton(text);
            KeyboardRow r1 = reply.getRows(b1);
            return reply.getKeyboard(r1, r2);
        }
        return reply.getKeyboard(r2);
    }

    protected ReplyKeyboardMarkup getRegionKeyboard(String lang) {
        boolean b = lang.equals(LANG_UZ);
        KeyboardRow r1 = reply.getRows(TASHKENT, TASHKENT_REGION);
        KeyboardRow r2 = reply.getRows(ANDIJAN_REGION, BUKHARA_REGION);
        KeyboardRow r3 = reply.getRows(JIZZAKH_REGION, KASHKADARYA_REGION);
        KeyboardRow r4 = reply.getRows(NAVOIY_REGION, NAMANGAN_REGION);
        KeyboardRow r5 = reply.getRows(SAMARKAND_REGION, SURKHANDARYA_REGION);
        KeyboardRow r6 = reply.getRows(SIRDARYA_REGION, FERGANA_REGION);
        KeyboardRow r7 = reply.getRows(KHOREZM_REGION, KARAKALPAKISTAN_REPUBLIC);
        KeyboardRow r8 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);


        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8);
    }

    protected boolean isValidMessage(String text) {

        Field[] declaredFields = UserMenu.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                if (text.equals(declaredField.get(declaredField.getName()).toString())) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                log.warn("<< UserMenu : " + e.getMessage());
            }
        }
        return true;
    }

    protected ReplyKeyboardMarkup getDistrictKeyboard(String lang, String text) {
        boolean b = lang.equals(LANG_UZ);
        switch (text) {
            case TASHKENT -> {
                return tashkentKeyboard(b);
            }
            case TASHKENT_REGION -> {
                return tashkentRegionKeyboard(b);
            }
            case ANDIJAN_REGION -> {
                return andijanKeyboard(b);
            }
            case FERGANA_REGION -> {
                return ferganaKeyboard(b);
            }
            case NAMANGAN_REGION -> {
                return namanganKeyboard(b);
            }
            case SIRDARYA_REGION -> {
                return sirdaryaKeyboard(b);
            }
            case JIZZAKH_REGION -> {
                return jizzakhKeyboard(b);
            }
            case SURKHANDARYA_REGION -> {
                return surkhandaryaKeyboard(b);
            }
            case KASHKADARYA_REGION -> {
                return kashkadaryaKeyboard(b);
            }
            case SAMARKAND_REGION -> {
                return samarkandKeyboard(b);
            }
            case BUKHARA_REGION -> {
                return buxoroKeyboard(b);
            }
            case NAVOIY_REGION -> {
                return navoiyKeyboard(b);
            }
            case KHOREZM_REGION -> {
                return khorezmKeyboard(b);
            }
            case KARAKALPAKISTAN_REPUBLIC -> {
                return karakalpakistanKeyboard(b);
            }
        }
        return null;
    }


    private ReplyKeyboardMarkup tashkentKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(BEKTEMIR, CHILONZOR);
        KeyboardRow r2 = reply.getRows(HAMZA, MIROBOD);
        KeyboardRow r3 = reply.getRows(MIRZO_ULUGBEK, SERGELI);
        KeyboardRow r4 = reply.getRows(SHAYXONTOHUR, OLMAZOR);
        KeyboardRow r5 = reply.getRows(UCHTEPA, YAKKASAROY);
        KeyboardRow r6 = reply.getRows(YUNUSABAD, YANGIHAYOT);
        KeyboardRow r7 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);

        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7);
    }

    private ReplyKeyboardMarkup tashkentRegionKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(T_BEKOBOD, T_BOSTONLIQ);
        KeyboardRow r2 = reply.getRows(T_BOKA, T_CHINOZ);
        KeyboardRow r3 = reply.getRows(T_QIBRAY, T_OHANGARON);
        KeyboardRow r4 = reply.getRows(T_OQQURGON, T_PARKENT);
        KeyboardRow r5 = reply.getRows(T_PISKENT, T_QUYI_CHIRCHIQ);
        KeyboardRow r6 = reply.getRows(T_ORTA_CHIRCHIQ, T_YANGIYOL);
        KeyboardRow r7 = reply.getRows(T_YUQORI_CHIRCHIQ, T_ZANGIOTA);
        KeyboardRow r8 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8);
    }

    private ReplyKeyboardMarkup andijanKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(A_ANDIJAN, A_ASAKA);
        KeyboardRow r2 = reply.getRows(A_BALIQCHI, A_BOZ);
        KeyboardRow r3 = reply.getRows(A_BULOQBOSHI, A_IZBOSKAN);
        KeyboardRow r4 = reply.getRows(A_JALAQUDUQ, A_XOJAOBOD);
        KeyboardRow r5 = reply.getRows(A_QORGONTEPA, A_MARHAMAT);
        KeyboardRow r6 = reply.getRows(A_OLTINKOL, A_PAXTAOBOD);
        KeyboardRow r7 = reply.getRows(A_SHAXRIXON, A_ULUGNOR);
        KeyboardRow r8 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8);
    }

    private ReplyKeyboardMarkup ferganaKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(F_FARGONA, F_OLTIARIQ);
        KeyboardRow r2 = reply.getRows(F_BAGDOD, F_BESHARIQ);
        KeyboardRow r3 = reply.getRows(F_BUVAYDA, F_DANGARA);
        KeyboardRow r4 = reply.getRows(F_FURQAT, F_QOSHTEPA);
        KeyboardRow r5 = reply.getRows(F_QUVA, F_RISHTON);
        KeyboardRow r6 = reply.getRows(F_SOX, F_TOSHLOQ);
        KeyboardRow r7 = reply.getRows(F_UCHKOPRIK, F_UZBEKISTAN);
        KeyboardRow r8 = reply.getRows(F_YOZYOVON, F_QUVASOY);

        KeyboardRow r9 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);

        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8, r9);
    }

    private ReplyKeyboardMarkup namanganKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(N_CHORTOQ, N_CHUST);
        KeyboardRow r2 = reply.getRows(N_KOSONSOY, N_MINGBULOQ);
        KeyboardRow r3 = reply.getRows(N_NAMANGAN, N_NORIN);
        KeyboardRow r4 = reply.getRows(N_POP, N_TORAQORGON);
        KeyboardRow r5 = reply.getRows(N_UCHQORGON, N_UYCHI);
        KeyboardRow r6 = reply.getRows(N_YANGIQORGON);
        KeyboardRow r7 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);

        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7);
    }

    private ReplyKeyboardMarkup sirdaryaKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(S_OQOLTIN, S_BOYOVUT);
        KeyboardRow r2 = reply.getRows(S_GULISTON_T, S_XOVOS);
        KeyboardRow r3 = reply.getRows(S_MIRZAOBOD, S_SAYXUNOBOD);
        KeyboardRow r4 = reply.getRows(S_SARDOBA, S_SIRDARYA);
        KeyboardRow r5 = reply.getRows(S_YANGIYER, S_SHIRIN);
        KeyboardRow r6 = reply.getRows(S_GULISTON_SH);
        KeyboardRow r7 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);

        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7);
    }

    private ReplyKeyboardMarkup jizzakhKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(J_ARNASOY, J_BAXMAL);
        KeyboardRow r2 = reply.getRows(J_DOSTLIK, J_FORISH);
        KeyboardRow r3 = reply.getRows(J_GALLAOROL, J_SHAROF_RASHIDOV);
        KeyboardRow r4 = reply.getRows(J_MIRZACHOL, J_PAXTAKOR);
        KeyboardRow r5 = reply.getRows(J_YANGIOBOD, J_ZOMIN);
        KeyboardRow r6 = reply.getRows(J_ZAFAROBOD, J_ZARBDOR);
        KeyboardRow r7 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);

        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7);
    }

    private ReplyKeyboardMarkup surkhandaryaKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(S_ANGOR, S_BOYSUN);
        KeyboardRow r2 = reply.getRows(S_DENOV, S_JARQORGON);
        KeyboardRow r3 = reply.getRows(S_QIZIRIQ, S_QUMQORGON);
        KeyboardRow r4 = reply.getRows(S_MUZRABOT, S_OLTINSOY);
        KeyboardRow r5 = reply.getRows(S_SARIOSIYO, S_SHEROBOD);
        KeyboardRow r6 = reply.getRows(S_SHORCHI, S_TERMIZ);
        KeyboardRow r7 = reply.getRows(S_UZUN);
        KeyboardRow r8 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8);
    }

    private ReplyKeyboardMarkup kashkadaryaKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(Q_CHIROQCHI, Q_DEHQONOBOD);
        KeyboardRow r2 = reply.getRows(Q_GUZOR, Q_QAMASHI);
        KeyboardRow r3 = reply.getRows(Q_QARSHI, Q_KOSON);
        KeyboardRow r4 = reply.getRows(Q_KASBI, Q_KITOB);
        KeyboardRow r5 = reply.getRows(Q_MIRISHKOR, Q_MUBORAK);
        KeyboardRow r6 = reply.getRows(Q_NISHON, Q_SHAHRISABZ);
        KeyboardRow r7 = reply.getRows(Q_YAKKABOG, Q_KOKDALA);
        KeyboardRow r8 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8);
    }

    private ReplyKeyboardMarkup samarkandKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(SA_BULUNGUR, SA_ISHTIXON);
        KeyboardRow r2 = reply.getRows(SA_JOMBOY, SA_KATTAQORGON);
        KeyboardRow r3 = reply.getRows(SA_QOSHRABOT, SA_NARPAY);
        KeyboardRow r4 = reply.getRows(SA_NUROBOD, SA_OQDARYO);
        KeyboardRow r5 = reply.getRows(SA_PAXTACHI, SA_PAYARIQ);
        KeyboardRow r6 = reply.getRows(SA_PASDARGOM, SA_SAMARQAND);
        KeyboardRow r7 = reply.getRows(SA_TOYLOQ, SA_URGUT);
        KeyboardRow r8 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8);
    }

    private ReplyKeyboardMarkup buxoroKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(B_OLOT, B_BUXORO);
        KeyboardRow r2 = reply.getRows(B_GIJDUVON, B_JONDOR);
        KeyboardRow r3 = reply.getRows(B_KOGON, B_QORAKOL);
        KeyboardRow r4 = reply.getRows(B_QOROVULBOZOR, B_PESHKU);
        KeyboardRow r5 = reply.getRows(B_ROMITAN, B_SHOFIRKON);
        KeyboardRow r6 = reply.getRows(B_VOBKENT);
        KeyboardRow r7 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7);
    }

    private ReplyKeyboardMarkup navoiyKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(NA_KONIMEX, NA_KARMANA);
        KeyboardRow r2 = reply.getRows(NA_QIZILTEPA, NA_XATIRCHI);
        KeyboardRow r3 = reply.getRows(NA_NAVBAHOR, NA_NUROTA);
        KeyboardRow r4 = reply.getRows(NA_TOMDI, NA_UCHQUDUQ);
        KeyboardRow r5 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5);
    }

    private ReplyKeyboardMarkup khorezmKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(KH_BOGOT, KH_GURLAN);
        KeyboardRow r2 = reply.getRows(KH_XONQA, KH_HAZORASP);
        KeyboardRow r3 = reply.getRows(KH_XIVA, KH_QOSHKOPIR);
        KeyboardRow r4 = reply.getRows(KH_SHOVOT, KH_URGANCH);
        KeyboardRow r5 = reply.getRows(KH_YANGIARIQ, KH_YANGIBOZOR);
        KeyboardRow r6 = reply.getRows(KH_TUPROQQALA);
        KeyboardRow r7 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);

        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7);
    }

    private ReplyKeyboardMarkup karakalpakistanKeyboard(boolean b) {
        KeyboardRow r1 = reply.getRows(KA_AMUDARYA, KA_BERUNIY);
        KeyboardRow r2 = reply.getRows(KA_CHIMBOY, KA_ELLIKQALA);
        KeyboardRow r3 = reply.getRows(KA_KEGEYLI, KA_MOYNOQ);
        KeyboardRow r4 = reply.getRows(KA_NUKUS, KA_QANLIKOL);
        KeyboardRow r5 = reply.getRows(KA_QONGIROT, KA_QORAOZAK);
        KeyboardRow r6 = reply.getRows(KA_SHUMANAY, KA_TAXTAKOPIR);
        KeyboardRow r7 = reply.getRows(KA_TORTKOL, KA_XOJAYLI);
        KeyboardRow r8 = reply.getRows(KA_TAXIATOSH, KA_BOZATOV);
        KeyboardRow r9 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
        return reply.getKeyboard(r1, r2, r3, r4, r5, r6, r7, r8, r9);
    }

    public InputFile getInputBot() {
        byte[] bytes = documentService.getExcelBot();
        InputStream stream = new ByteArrayInputStream(bytes);
        InputFile file = new InputFile();
        file.setMedia(stream, "очиқ-мулоқот.xlsx");
        return file;
    }


    public InputFile getInputGoogleForm() {
        byte[] bytes = documentService.getExcelGoogleForm();
        InputStream stream = new ByteArrayInputStream(bytes);
        InputFile file = new InputFile();
        file.setMedia(stream, "google-forms.xlsx");
        return file;
    }
}
