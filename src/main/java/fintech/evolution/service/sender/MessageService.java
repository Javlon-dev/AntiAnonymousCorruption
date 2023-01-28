package fintech.evolution.service.sender;

import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.entity.user.UserDebate;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Field;
import java.util.List;

import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;
import static fintech.evolution.variable.constants.user.UserStep.STEP_SET_SURNAME;
import static fintech.evolution.variable.constants.user.UserStep.STEP_START;

@Service
@Slf4j
public class MessageService extends AbstractService {
    @Value("${admin.chatId:0}")
    private String adminChatId;

    public List<GeneralSender> start(Long chatId, Message message) {
        if (message.getText().equals("/start")) service.setStep(chatId, STEP_START);
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);
        if (isValidMessage(message.getText())) {
            switch (step) {
            }
        }
        return null;
    }

    private List<GeneralSender> getRegistration(Long chatId) {
        boolean b = service.getLang(chatId).equals(LANG_UZ);
        String ms = b ? "<b>Исмингизни киритинг!</b>" : "<b>Введите ваше имя!</b>";

        return List.of(SenderMessage
                .builder()
                .reply(getKeyboardWithCancel(chatId, null))
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .text(ms).build());
    }

    private List<GeneralSender> setNameFunction(Long chatId, String text) {
        UserDebate userDebate = service.getUserDebate(chatId);
        userDebate.setName(text);
        service.setUserDebate(chatId, userDebate);
        service.setStep(chatId, STEP_SET_SURNAME);

        boolean b = service.getLang(chatId).equals(LANG_UZ);
        String ms = b ? "<b>Фамилиянгизни киритинг!</b>" : "<b>Введите свою фамилию!</b>";


        return List.of(SenderMessage
                .builder()
                .reply(getKeyboardWithCancel(chatId, null))
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .text(ms).build());
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
