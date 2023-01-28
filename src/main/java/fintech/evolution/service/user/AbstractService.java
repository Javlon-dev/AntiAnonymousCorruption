package fintech.evolution.service.user;

import fintech.evolution.service.DocumentService;
import fintech.evolution.service.keyboard.InlineKeyboardService;
import fintech.evolution.service.keyboard.ReplyKeyboardService;
import fintech.evolution.service.sender.ExecutorService;
import fintech.evolution.variable.constants.user.UserMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static fintech.evolution.utils.Utils.getTextByLang;
import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;
import static fintech.evolution.variable.constants.user.UserMenu.*;

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

    protected ReplyKeyboardMarkup getKeyboardWithRequestContact(Long chatId, String text, boolean oneTime) {
        ReplyKeyboardMarkup keyboard = getKeyboardWithRequestContact(chatId, text);
        keyboard.setOneTimeKeyboard(oneTime);
        return keyboard;
    }

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
        KeyboardButton b2 = reply.getButton(getTextByLang(lang, MENU_CANCEL_UZ, MENU_CANCEL_RU));
        KeyboardRow r2 = reply.getRows(b2);
        if (text != null) {
            KeyboardButton b1 = reply.getButton(text);
            KeyboardRow r1 = reply.getRows(b1);
            return reply.getKeyboard(r1, r2);
        }
        return reply.getKeyboard(r2);
    }

    protected ReplyKeyboardMarkup getKeyboardWithBack(Long chatId, String text) {
        String lang = service.getLang(chatId);
        KeyboardButton b2 = reply.getButton(getTextByLang(lang, MENU_BACK_UZ, MENU_BACK_RU));
        KeyboardRow r2 = reply.getRows(b2);
        if (text != null) {
            KeyboardButton b1 = reply.getButton(text);
            KeyboardRow r1 = reply.getRows(b1);
            return reply.getKeyboard(r1, r2);
        }
        return reply.getKeyboard(r2);
    }

    protected boolean isFormative(Class<?> clas, String text) {

        Field[] declaredFields = clas.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                if (text.equals(declaredField.get(declaredField.getName()).toString())) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                log.warn("<< UserMenu : " + e.getMessage());
            }
        }
        return false;
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
    protected boolean isFormalEmail(String email) {
        String mail = "^[_A-Za-z\\d-+]+(.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(.[A-Za-z\\d]+)*(.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(mail);
        return pattern.matcher(email).matches();
    }

    protected boolean isFormalContact(String contact) {
        String phone = "\\+998\\d{9}";
        Pattern pattern = Pattern.compile(phone);
        return pattern.matcher(contact).matches();
    }
}
