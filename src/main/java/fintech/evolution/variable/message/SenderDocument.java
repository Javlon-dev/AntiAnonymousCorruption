package fintech.evolution.variable.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import fintech.evolution.variable.enums.MessageType;

@Getter
@Setter
@Builder
public class SenderDocument implements GeneralSender {

    private Long chatId;
    private String parseMode;
    private ReplyKeyboard reply;
    private Integer replyMessageId;
    private String caption;
    private InputFile inputFile;

    @Override
    public MessageType getType() {
        return MessageType.SEND_DOCUMENT;
    }
}
