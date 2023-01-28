package fintech.evolution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.*;
import fintech.evolution.service.sender.CallBackQueryService;
import fintech.evolution.service.sender.MessageService;
import fintech.evolution.service.user.UserService;
import fintech.evolution.variable.enums.UpdateEnum;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderLocation;
import fintech.evolution.variable.message.SenderMessage;

import java.util.List;

import static fintech.evolution.variable.constants.user.UserStep.*;
import static fintech.evolution.variable.enums.UpdateEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {
    private final MessageService messageService;
    private final CallBackQueryService callBackQueryService;
    private final UserService userService;
    private final DocumentService documentService;


    public List<GeneralSender> onUpdate(Update update) {
        UpdateEnum updateEnum = getUpdate(update);


        switch (updateEnum) {
            case MESSAGE_TEXT -> {
                return messageText(update.getMessage());
            }
            case CALL_BACK_QUERY -> {
                return callBack(update.getCallbackQuery());
            }

            case CONTACT_MESSAGE -> {

                return messageContact(update.getMessage());
            }
            case DEFAULT_UPDATE -> {
                return null;
            }
        }

        return null;
    }


    private List<GeneralSender> messageText(Message message) {
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        userService.setMessageId(chatId, messageId);

        return messageService.start(chatId, message);
    }

    private List<GeneralSender> callBack(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        userService.setMessageId(chatId, messageId);

        return callBackQueryService.start(chatId, callbackQuery);
    }

    private List<GeneralSender> messageContact(Message message) {
        Long chatId = message.getChatId();
        Contact contact = message.getContact();
        String step = userService.getStep(chatId);

        return null;
    }




    private UpdateEnum getUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) return MESSAGE_TEXT;
            if (message.hasVoice()) return VOICE_MESSAGE;
            if (message.hasPhoto()) return PHOTO_MESSAGE;
            if (message.hasDocument()) return DOCUMENT_MESSAGE;
            if (message.hasContact()) return CONTACT_MESSAGE;
            if (message.hasAnimation()) return ANIMATION_MESSAGE;
            if (message.hasAudio()) return AUDIO_MESSAGE;
            if (message.hasLocation()) return LOCATION_MESSAGE;
        }
        if (update.hasCallbackQuery()) return CALL_BACK_QUERY;
        return DEFAULT_UPDATE;
    }

    public InputFile getInputGoogleForm() {
        return callBackQueryService.getInputGoogleForm();
    }

    public InputFile getInputBot() {
        return callBackQueryService.getInputBot();
    }


    public void onDocument(String s) {
        documentService.getUrl(s);
    }
}
