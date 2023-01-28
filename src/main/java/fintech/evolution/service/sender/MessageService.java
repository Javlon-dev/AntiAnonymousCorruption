package fintech.evolution.service.sender;

import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.entity.UserCooperation;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static fintech.evolution.utils.Utils.getTextByLang;
import static fintech.evolution.variable.constants.user.UserMenu.*;
import static fintech.evolution.variable.constants.user.UserStep.*;
import static fintech.evolution.variable.constants.user.UserText.*;

@Service
@Slf4j
public class MessageService extends AbstractService {

    @Value("${admin.chatId:0}")
    private Long adminChatId;

    public List<GeneralSender> start(Long chatId, Message message) {
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);
        if (isValidMessage(message.getText())) {
            switch (step) {
                case STEP_START -> {
                    return stepStart(chatId, getTextByLang(lang, TEXT_START_UZ, TEXT_START_RU));
                }
                case STEP_COOPERATION_FULL_NAME -> {
                    return stepCooperation(chatId, message.getText());
                }
                case STEP_COOPERATION_PHONE -> {

                }
            }
        } else {
            switch (message.getText()) {
                case MENU_COOPERATION_UZ, MENU_COOPERATION_RU -> {
                    if (step.equals(STEP_START)) {
                        service.setStep(chatId, STEP_COOPERATION_PHONE);
                        return getCooperation(chatId);
                    }
                }
                case MENU_STATISTICS_UZ, MENU_STATISTICS_RU -> {
                    if (step.equals(STEP_START)) {
                        service.setStep(chatId, STEP_COOPERATION_PHONE);
                        return getCooperation(chatId);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    public List<GeneralSender> getCooperation(Long chatId) {
        String text = getTextByLang(service.getLang(chatId), TEXT_FULL_NAME_UZ, TEXT_FULL_NAME_RU);
        service.setStep(chatId, STEP_COOPERATION_PHONE);
        SenderMessage senderMessage = SenderMessage
                .builder()
                .reply(getKeyboardWithCancel(chatId, null))
                .chatId(chatId)
                .text(text).build();

        return Collections.singletonList(senderMessage);

    }

    public List<GeneralSender> stepCooperation(Long chatId, String msg) {
        if (msg.length() <= 2) return getCooperation(chatId);

        String lang = service.getLang(chatId);
        UserCooperation userCooperation = service.getUserCooperation(chatId);
        userCooperation.setFullName(msg);
        service.setUserCooperation(chatId, userCooperation);
        service.setStep(chatId, STEP_COOPERATION_PHONE);

        String text = getTextByLang(lang, TEXT_PHONE_UZ, TEXT_PHONE_RU);


        return List.of(SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build());
    }

    public List<GeneralSender> stepStart(Long chatId, String text) {
        String lang = service.getLang(chatId);
        String cooperation = getTextByLang(lang, MENU_COOPERATION_UZ, MENU_COOPERATION_RU);
        String statistics = getTextByLang(lang, MENU_STATISTICS_UZ, MENU_STATISTICS_RU);
        String documents = getTextByLang(lang, MENU_DOCUMENTS_UZ, MENU_DOCUMENTS_RU);
        String settings = getTextByLang(lang, MENU_SETTINGS_UZ, MENU_SETTINGS_RU);
        KeyboardButton b1 = reply.getButton(cooperation);
        KeyboardButton b2 = reply.getButton(statistics);
        KeyboardButton b3 = reply.getButton(documents);
        KeyboardButton b4 = reply.getButton(settings);
        KeyboardRow r1 = reply.getRows(b1, b2);
        KeyboardRow r2 = reply.getRows(b3, b4);
        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2);

        SenderMessage senderMessage = SenderMessage.builder().text(text).chatId(chatId).reply(keyboard).build();
        return Collections.singletonList(senderMessage);
    }


    private boolean isFormative(Class<?> clas, String text) {

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
}
