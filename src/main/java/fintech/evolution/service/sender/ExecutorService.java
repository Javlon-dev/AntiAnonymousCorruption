package fintech.evolution.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import fintech.evolution.variable.message.GeneralSender;

@Slf4j
@Service
public class ExecutorService {

    @Value("${channel.chat.id}")
    private Long channelChatId;

    public SendMessage send(GeneralSender sender) {
        var send = SendMessage
                .builder()
                .chatId(sender.getChatId())
                .text(sender.getText())
                .parseMode(sender.getParseMode() == null ? ParseMode.MARKDOWN : sender.getParseMode())
                .disableWebPagePreview(sender.getDisableWebPagePreview())
                .build();
        if (sender.getReply() != null) send.setReplyMarkup(sender.getReply());
        if (isExistReplyMessage(sender)) send.setReplyToMessageId(sender.getReplyMessageId());
        return send;
    }

    public EditMessageText edit(GeneralSender sender) {
        var send = EditMessageText
                .builder()
                .chatId(sender.getChatId())
                .text(sender.getText())
                .messageId(sender.getMessageId())
                .parseMode(sender.getParseMode() == null ? ParseMode.MARKDOWN : sender.getParseMode())
                .build();
        if (isExistKeyboard(sender)) {
            InlineKeyboardMarkup reply = (InlineKeyboardMarkup) sender.getReply();
            send.setReplyMarkup(reply);
        }
        return send;
    }

    public SendPhoto sendPhoto(GeneralSender sender) {

        var send = SendPhoto
                .builder()
                .chatId(sender.getChatId())
                .parseMode(sender.getParseMode() == null ? ParseMode.MARKDOWN : sender.getParseMode())
                .photo(sender.getInputFile())
                .caption(sender.getCaption())
                .build();
        if (isExistKeyboard(sender)) send.setReplyMarkup(sender.getReply());
        if (isExistReplyMessage(sender)) send.setReplyToMessageId(sender.getReplyMessageId());
        return send;
    }

    public SendLocation sendLocation(GeneralSender sender) {
        var send = SendLocation.builder()
                .chatId(sender.getChatId())
                .latitude(sender.getLatitude())
                .longitude(sender.getLongitude())
                .build();
        if (isExistKeyboard(sender)) send.setReplyMarkup(sender.getReply());
        if (isExistReplyMessage(sender)) send.setReplyToMessageId(sender.getReplyMessageId());
        return send;
    }

    public SendContact sendContact(GeneralSender sender) {
        var send = SendContact
                .builder()
                .chatId(sender.getChatId())
                .firstName(sender.getFirstName())
                .lastName(sender.getLastName())
                .phoneNumber(sender.getPhoneNumber())
                .build();
        if (isExistKeyboard(sender)) send.setReplyMarkup(sender.getReply());
        if (isExistReplyMessage(sender)) send.setReplyToMessageId(sender.getReplyMessageId());
        return send;
    }

    public ForwardMessage forward(GeneralSender sender) {

        return ForwardMessage
                .builder()
                .chatId(sender.getChatId())
                .fromChatId(sender.getFromChatId())
                .messageId(sender.getMessageId())
                .build();
    }

    private boolean isExistReplyMessage(GeneralSender sender) {
        return sender.getMessageId() != null;
    }

    private boolean isExistKeyboard(GeneralSender sender) {
        return sender.getReply() != null;
    }

    public SendDocument sendDocument(GeneralSender sender) {
        var send = SendDocument
                .builder()
                .chatId(sender.getChatId())
                .parseMode(sender.getParseMode() == null ? ParseMode.MARKDOWN : sender.getParseMode())
                .document(sender.getInputFile())
                .caption(sender.getCaption())
                .build();
        if (isExistKeyboard(sender)) send.setReplyMarkup(sender.getReply());
        if (isExistReplyMessage(sender)) send.setReplyToMessageId(sender.getReplyMessageId());
        return send;
    }
}
