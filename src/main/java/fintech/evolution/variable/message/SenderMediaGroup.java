package fintech.evolution.variable.message;

import fintech.evolution.variable.enums.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.util.List;

@Getter
@Setter
@Builder
public class SenderMediaGroup implements GeneralSender {

    private Long chatId;

    private List<InputMedia> medias;

    @Override
    public MessageType getType() {
        return MessageType.SEND_MEDIA_GROUP;
    }

}
