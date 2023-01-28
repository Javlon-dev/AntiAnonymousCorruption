package fintech.evolution.service.sender;

import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.message.GeneralSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author : Khonimov Ulugbek
 * Date : 1/28/2023
 */
@Service
public class AdminService extends AbstractService {

    public List<GeneralSender> start(Message message) {
        return Collections.emptyList();
    }

    public List<GeneralSender> callBack(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
