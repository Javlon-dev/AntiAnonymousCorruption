package fintech.evolution.service.sender;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.entity.user.UserDebate;
import fintech.evolution.variable.message.GeneralSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CallBackQueryService extends AbstractService {

    public List<GeneralSender> start(Long chatId, CallbackQuery callbackQuery) {

        return Collections.emptyList();
    }

    public boolean delete(Long stir) {


        return false;
    }
}
