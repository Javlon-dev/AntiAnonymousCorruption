package fintech.evolution.service.sender;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.entity.user.UserDebate;
import fintech.evolution.variable.message.GeneralSender;

import java.util.Optional;

@Service
public class CallBackQueryService extends AbstractService {

    public GeneralSender start(Long chatId, CallbackQuery callbackQuery) {

        return null;
    }

    public boolean delete(Long stir) {

        Optional<UserDebate> entity = userDebateRepository.findByStir(stir);
        if (entity.isPresent()) {
            userDebateRepository.delete(entity.get());
            return true;
        }
        return false;
    }
}
