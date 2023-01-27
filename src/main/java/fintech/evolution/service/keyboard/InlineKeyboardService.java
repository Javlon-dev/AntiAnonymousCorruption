package fintech.evolution.service.keyboard;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineKeyboardService {


    public InlineKeyboardButton getButton(String text, String callBackData) {
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(text);
        keyboardButton.setCallbackData(callBackData);
        return keyboardButton;
    }


    public List<InlineKeyboardButton> getButtonList(InlineKeyboardButton... buttons) {
        return new ArrayList<>(List.of(buttons));
    }


    @SafeVarargs
    public final InlineKeyboardMarkup getKeyboard(List<InlineKeyboardButton>... lists) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(new ArrayList<>(List.of(lists)));
        return inlineKeyboardMarkup;
    }
}
