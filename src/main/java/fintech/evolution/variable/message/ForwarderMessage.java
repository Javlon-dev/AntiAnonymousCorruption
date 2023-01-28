package fintech.evolution.variable.message;

import fintech.evolution.variable.enums.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Author : Khonimov Ulugbek
 * Date : 10.12.2022
 * Time : 8:25 AM
 */

@Getter
@Setter
@Builder
public class ForwarderMessage implements GeneralSender {
    private Long chatId;
    private Long fromChatId;
    private Integer messageId;

    @Override
    public MessageType getType() {
        return MessageType.FORWARD_MESSAGE;
    }
}