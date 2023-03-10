package fintech.evolution.service;

import fintech.evolution.service.sender.AdminService;
import fintech.evolution.service.sender.CallBackQueryService;
import fintech.evolution.service.sender.MessageService;
import fintech.evolution.service.user.UserService;
import fintech.evolution.variable.enums.UpdateEnum;
import fintech.evolution.variable.message.ForwarderMessage;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderMediaGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.util.Collections;
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
    private final AdminService adminService;
    @Value("${admin.chat.id:0}")
    private Long adminChatId;


    public List<GeneralSender> onUpdate(Update update) {
        UpdateEnum updateEnum = getUpdate(update);
        log.info("<< onUpdate " + update);

        switch (updateEnum) {
            case MESSAGE_TEXT -> {
                if (update.getMessage().getChatId().equals(adminChatId))
                    return adminService.start(update.getMessage());
                return messageText(update.getMessage());
            }
            case CALL_BACK_QUERY -> {
                if (update.getCallbackQuery().getMessage().getChatId().equals(adminChatId))
                    return adminService.callBack(update.getCallbackQuery());
                return callBack(update.getCallbackQuery());
            }
            case CONTACT_MESSAGE -> {
                return messageContact(update.getMessage());
            }
            case PHOTO_MESSAGE, VIDEO_MESSAGE -> {
                return messagePhotoOrVideo(update.getMessage());
            }
            case DEFAULT_UPDATE -> {
                return Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }

    public List<GeneralSender> messagePhotoOrVideo(Message message) {
        Long chatId = message.getChatId();
        String step = userService.getStep(chatId);

        if (step.equals(STEP_COOPERATION)) return messageService.stepCooperation(chatId, message.getMessageId());
        return Collections.emptyList();
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

        if (step.equals(STEP_COOPERATION_PHONE)) return messageService.stepContact(chatId, contact);
        return Collections.emptyList();
    }


    private UpdateEnum getUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) return MESSAGE_TEXT;
            if (message.hasVoice()) return VOICE_MESSAGE;
            if (message.hasPhoto()) return PHOTO_MESSAGE;
            if (message.hasVideo()) return VIDEO_MESSAGE;
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

    }
}
