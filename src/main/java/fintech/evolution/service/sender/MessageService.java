package fintech.evolution.service.sender;

import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.constants.user.UserMenu;
import fintech.evolution.variable.entity.UserCooperation;
import fintech.evolution.variable.message.ForwarderMessage;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fintech.evolution.utils.Utils.getTextByLang;
import static fintech.evolution.utils.Utils.isFormalContact;
import static fintech.evolution.variable.constants.user.UserLang.*;
import static fintech.evolution.variable.constants.user.UserMenu.*;
import static fintech.evolution.variable.constants.user.UserStep.*;
import static fintech.evolution.variable.constants.user.UserText.*;

@Service
@Slf4j
public class MessageService extends AbstractService {

    @Value("${admin.chat.id:0}")
    private Long adminChatId;

    @Value("${channel.chat.id:0}")
    private Long channelChatId;

    public List<GeneralSender> start(Long chatId, Message message) {
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);
        boolean b = step.equals(STEP_AUTO);

        if (message.getText().equals(TEXT_START) || b) {
            return startMenu(chatId, getTextByLang(lang, TEXT_CHOOSE_UZ, TEXT_CHOOSE_RU));
        }

        if (isFormative(UserMenu.class, message.getText())) {
            return formativeMenu(chatId, message);
        } else {
            return stepMenu(chatId, message);
        }
    }

    private List<GeneralSender> stepMenu(Long chatId, Message message) {
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);

        switch (step) {
            case STEP_COOPERATION_FULL_NAME -> {
                return stepCooperationFullName(chatId, message);
            }
            case STEP_COOPERATION_PHONE -> {
                return stepCooperationPhone(chatId, message.getText());
            }
            case STEP_SETTINGS -> {
                return stepSettings(chatId, getTextByLang(lang, TEXT_CHOOSE_UZ, TEXT_CHOOSE_RU));
            }
            case STEP_STATISTICS -> {
                return stepStatistics(chatId, message.getText());
            }
        }

        return Collections.emptyList();
    }

    public List<GeneralSender> stepStatistics(Long chatId, String text) {
        return Collections.emptyList();
    }

    public List<GeneralSender> stepCooperationPhone(Long chatId, String msg) {
        String lang = service.getLang(chatId);
        if ((msg.length() == 9 && !isFormalContact(msg = "+998" + msg) || !isFormalContact(msg))) {
            return Collections.singletonList(SenderMessage
                    .builder()
                    .chatId(chatId)
                    .text(getTextByLang(lang, TEXT_COOPERATION_PHONE_UZ, TEXT_COOPERATION_PHONE_RU))
                    .reply(getKeyboardWithRequestContact(chatId, getTextByLang(lang, TEXT_SHARE_CONTACT_UZ, TEXT_SHARE_CONTACT_RU)))
                    .build());
        }
        UserCooperation userCooperation = service.getUserCooperation(chatId);
        userCooperation.setPhoneNumber(msg);
        service.setUserCooperation(chatId, userCooperation);
        service.setStep(chatId, STEP_COOPERATION);

        String text = getTextByLang(lang, TEXT_COOPERATION_SEND_UZ, TEXT_COOPERATION_SEND_RU);

        return Collections.singletonList(SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build());
    }

    private List<GeneralSender> formativeMenu(Long chatId, Message message) {
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);

        switch (message.getText()) {
            case MENU_COOPERATION_UZ, MENU_COOPERATION_RU -> {
                if (step.equals(STEP_START)) {
                    service.setStep(chatId, STEP_COOPERATION_FULL_NAME);
                    return getCooperation(chatId);
                }
            }
            case MENU_STATISTICS_UZ, MENU_STATISTICS_RU -> {
                if (step.equals(STEP_START)) {
                    service.setStep(chatId, STEP_STATISTICS);
                    return getStatistics(chatId);
                }
            }
            case MENU_BACK_UZ, MENU_BACK_RU -> {
                return backMenu(chatId);
            }
            case MENU_CHANGE_LANG_UZ, MENU_CHANGE_LANG_RU -> {
                service.setStep(chatId, STEP_CHANGE_LANG);
                return changeLangMenu(chatId, null);
            }
            case MENU_SETTINGS_UZ, MENU_SETTINGS_RU -> {
                service.setStep(chatId, STEP_SETTINGS);
                return stepSettings(chatId, getTextByLang(lang, TEXT_CHOOSE_UZ, TEXT_CHOOSE_RU));
            }
            case MENU_SELECTED_LANG_UZ, MENU_SELECTED_LANG_RU -> {
                if (step.equals(STEP_CHANGE_LANG)) {
                    if (message.getText().equals(MENU_SELECTED_LANG_UZ)) {
                        service.setLang(chatId, LANG_UZ);
                    } else {
                        service.setLang(chatId, LANG_RU);
                    }
                    lang = service.getLang(chatId);
                    String msg = lang.equals(LANG_UZ) ? TEXT_SELECTED_LANG_UZ : lang.equals(LANG_RU) ? TEXT_SELECTED_LANG_RU : "";
                    return changeLangMenu(chatId, msg);
                }
            }
            case MENU_CANCEL_UZ, MENU_CANCEL_RU -> {
                return menuCancel(chatId);
            }
        }

        return Collections.emptyList();
    }

    public List<GeneralSender> menuCancel(Long chatId) {
        String lang = service.getLang(chatId);
        String step = service.getStep(chatId);
        if (step.equals(STEP_START)) {
            return Collections.emptyList();
        }
        service.setUserCooperation(chatId, null);
        service.setStep(chatId, STEP_START);

        return startMenu(chatId, getTextByLang(lang, TEXT_CHOOSE_UZ, TEXT_CHOOSE_RU));
    }

    private List<GeneralSender> getStatistics(Long chatId) {
        String text = getTextByLang(service.getLang(chatId), TEXT_STATISTICS_UZ, TEXT_STATISTICS_RU);

        SenderMessage senderMessage = SenderMessage
                .builder()
                .reply(getKeyboardWithBack(chatId, null))
                .chatId(chatId)
                .text(text)
                .build();

        return Collections.singletonList(senderMessage);
    }

    public List<GeneralSender> backMenu(Long chatId) {
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);

        switch (step) {
            case STEP_CHANGE_LANG -> {
                service.setStep(chatId, STEP_SETTINGS);
                return stepSettings(chatId, getTextByLang(lang, TEXT_CHOOSE_UZ, TEXT_CHOOSE_RU));
            }
            case STEP_START, STEP_COOPERATION_FULL_NAME, STEP_STATISTICS, STEP_DOCUMENTS, STEP_SETTINGS -> {
                return startMenu(chatId, getTextByLang(lang, TEXT_CHOOSE_UZ, TEXT_CHOOSE_RU));
            }
        }

        return Collections.emptyList();
    }

    private List<GeneralSender> stepSettings(Long chatId, String msg) {
        String lang = service.getLang(chatId);

        String changeLang = getTextByLang(lang, MENU_CHANGE_LANG_UZ, MENU_CHANGE_LANG_RU);
        String back = getTextByLang(lang, MENU_BACK_UZ, MENU_BACK_RU);

        KeyboardButton b1 = reply.getButton(changeLang);
        KeyboardButton b2 = reply.getButton(back);

        KeyboardRow r1 = reply.getRows(b1);
        KeyboardRow r2 = reply.getRows(b2);

        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2);

        SenderMessage senderMessage = SenderMessage
                .builder()
                .text(msg)
                .chatId(chatId)
                .reply(keyboard)
                .build();

        return Collections.singletonList(senderMessage);
    }

    public List<GeneralSender> changeLangMenu(Long chatId, String text) {
        String lang = service.getLang(chatId);
        String back = lang.equals(LANG_UZ) ? MENU_BACK_UZ : MENU_BACK_RU;

        KeyboardButton btnUz = reply.getButton(MENU_SELECTED_LANG_UZ);
        KeyboardButton btnRu = reply.getButton(MENU_SELECTED_LANG_RU);
        KeyboardButton btnBack = reply.getButton(back);
        KeyboardRow r1 = reply.getRows(btnUz, btnRu);
        KeyboardRow r2 = reply.getRows(btnBack);
        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2);
        String msg = text != null ? text : getTextByLang(lang, TEXT_LANG_UZ, TEXT_LANG_RU);;

        SenderMessage senderMessage = SenderMessage
                .builder()
                .text(msg)
                .reply(keyboard)
                .chatId(chatId)
                .build();

        return Collections.singletonList(senderMessage);
    }

    public List<GeneralSender> getCooperation(Long chatId) {
        String text = getTextByLang(service.getLang(chatId), TEXT_FULL_NAME_UZ, TEXT_FULL_NAME_RU);
        SenderMessage senderMessage = SenderMessage
                .builder()
                .reply(getKeyboardWithCancel(chatId, null))
                .chatId(chatId)
                .text(text).build();

        return Collections.singletonList(senderMessage);

    }

    public List<GeneralSender> stepCooperationFullName(Long chatId, Message message) {
        String msg = message.getText();
        if (msg.length() <= 2) return getCooperation(chatId);

        String lang = service.getLang(chatId);
        UserCooperation userCooperation = service.getUserCooperation(chatId);
        userCooperation.setFullName(msg);
        userCooperation.setUsername(message.getFrom().getUserName());
        service.setUserCooperation(chatId, userCooperation);
        service.setStep(chatId, STEP_COOPERATION_PHONE);

        String text = getTextByLang(lang, TEXT_COOPERATION_PHONE_UZ, TEXT_COOPERATION_PHONE_RU);


        return Collections.singletonList(SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .reply(getKeyboardWithRequestContact(chatId, getTextByLang(lang, TEXT_SHARE_CONTACT_UZ, TEXT_SHARE_CONTACT_RU)))
                .build());
    }

    public List<GeneralSender> startMenu(Long chatId, String text) {
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);
        service.setStep(chatId, STEP_START);

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

        List<GeneralSender> list;
        if (step.equals(STEP_AUTO)) {
            list = new ArrayList<>(Collections.singleton(SenderMessage
                    .builder()
                    .text(getTextByLang(lang, TEXT_START_UZ, TEXT_START_RU))
                    .chatId(chatId)
                    .build()));
            list.add(senderMessage);
            return list;
        } else {
            return Collections.singletonList(senderMessage);
        }
    }

    public List<GeneralSender> stepContact(Long chatId, Contact contact) {
        String lang = service.getLang(chatId);

        UserCooperation userCooperation = service.getUserCooperation(chatId);
        userCooperation.setPhoneNumber(contact.getPhoneNumber());
        service.setUserCooperation(chatId, userCooperation);
        service.setStep(chatId, STEP_COOPERATION);

        String text = getTextByLang(lang, TEXT_COOPERATION_SEND_UZ, TEXT_COOPERATION_SEND_RU);

        return Collections.singletonList(SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .reply(getKeyboardWithCancel(chatId, null))
                .build());
    }

    public List<GeneralSender> stepCooperation(Long chatId, Integer messageId) {
        String lang = service.getLang(chatId);

        UserCooperation userCooperation = service.getUserCooperation(chatId);
        userCooperation.setAttachId(messageId);
        service.setUserCooperation(chatId, userCooperation);

        List<GeneralSender> list = new ArrayList<>(
                startMenu(chatId, getTextByLang(lang, TEXT_CONGRATS_UZ, TEXT_CONGRATS_RU)));

        list.add(ForwarderMessage
                .builder()
                .chatId(channelChatId)
                .fromChatId(chatId)
                .messageId(messageId)
                .build());

        list.add(SenderMessage
                .builder()
                .chatId(channelChatId)
                .text(getCooperationUser(userCooperation))
                .disableWebPagePreview(true)
                .build());

        return list;
    }

    private String getCooperationUser(UserCooperation userCooperation) {
        return """
                *F.I.O:* `%s`
                *Phone:* `%s`
                *Username:* %s
                """.formatted(userCooperation.getFullName(),
                userCooperation.getPhoneNumber(),
                userCooperation.getUsername() != null ? "https://t.me/" + userCooperation.getUsername() : "https://t.me/" + userCooperation.getPhoneNumber());
    }
}
