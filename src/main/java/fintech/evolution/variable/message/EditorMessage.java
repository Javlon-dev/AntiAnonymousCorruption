package fintech.evolution.variable.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import fintech.evolution.variable.enums.MessageType;

@Getter
@Setter
@Builder
public class EditorMessage implements GeneralSender {
    private Long chatId;
    private String text;
    private String parseMode;
    private ReplyKeyboard reply;
    private Integer messageId;

    @Override
    public MessageType getType() {
        return MessageType.EDIT_MESSAGE;
    }
}
