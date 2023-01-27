package fintech.evolution.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import fintech.evolution.config.BotConfig;
import fintech.evolution.service.sender.ExecutorService;
import fintech.evolution.service.user.UserService;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderDocument;
import fintech.evolution.variable.message.SenderMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.telegram.telegrambots.meta.api.methods.ActionType.*;
import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final ExecutorService executorService;
    private final TelegramService service;
    private final UserService userService;


    @Value("${group.chatId:0}")
    private String groupChatId;
    @Value("${admin.chatId:0}")
    private String adminChatId;

    @Value("${secret.key:noKey}")
    private String secretKey;

    public TelegramBot(BotConfig config, ExecutorService executorService, TelegramService service, UserService userService) {
        this.config = config;
        this.executorService = executorService;
        this.service = service;
        this.userService = userService;

        List<BotCommand> list = new ArrayList<>();
        list.add(new BotCommand("/start", "Botni ishga tushirish."));
        try {
            this.execute(new SetMyCommands(list, new BotCommandScopeDefault(), null));

        } catch (TelegramApiException e) {
            log.error("Error occurred in commands: " + e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        GeneralSender sender = service.onUpdate(update);

        if (update.hasMessage()) {
            if (update.getMessage().hasDocument()) {
                var doc = update.getMessage().getDocument();
                if (doc.getFileName().endsWith(".xlsx")){
                    GetFile getFile = new GetFile();
                    getFile.setFileId(doc.getFileId());
                    try {
                        var file = execute(getFile);
                        var s = file.getFileUrl(config.getBotToken());
                        service.onDocument(s);
                        LocalDate date = LocalDate.now();
                        String time = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        executeMessage(SenderDocument
                                .builder()
                                .chatId(Long.valueOf(groupChatId))
                                .caption(time + " ҳолатига кўра.")
                                .inputFile(service.getInputGoogleForm())
                                .build());

                    } catch (Exception e) {
                        System.out.println("Asd");
                    }
                }
            }
        }

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(secretKey)
                        && update.getMessage().getChatId().equals(Long.valueOf(groupChatId))) {
                    LocalDate date = LocalDate.now();
                    String time = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    executeMessage(SenderDocument
                            .builder()
                            .chatId(Long.valueOf(groupChatId))
                            .caption(time + " ҳолатига кўра.")
                            .inputFile(service.getInputBot())
                            .build());
                }

                if (update.getMessage().getText().startsWith("delete ")) {
                    if (update.getMessage().getChatId().equals(Long.valueOf(groupChatId))
                            || update.getMessage().getChatId().equals(Long.valueOf(adminChatId))) {
                        String[] s = update.getMessage().getText().split(" ");

                        executeMessage(service.delete(s[1]
                                , Long.valueOf(groupChatId)));
                    }
                }

                if (update.getMessage().getText().equals("/start")) {
                    String uz = "<b>Ассалому алайкум!</b>";
                    String ru = "<b>Здравствуйте!</b>";
                    String lang = userService.getLang(sender.getChatId());

                    executeMessage(SenderMessage
                            .builder()
                            .chatId(sender.getChatId())
                            .text(lang.equals(LANG_UZ) ? uz : ru)
                            .parseMode(ParseMode.HTML)
                            .build());

                }
            }
        }
        if (sender != null) {
            if (sender.getChatId().equals(Long.valueOf(groupChatId))) {
                sender = null;
            }
        }
        if (sender != null) {
            executeMessage(sender);
        }
    }

    private void executeMessage(GeneralSender sender) {
        try {

            switch (sender.getType()) {
                case SEND_MESSAGE -> {
                    execute(sendAction(sender.getChatId(), TYPING));
                    execute(executorService.send(sender));
                }
                case EDIT_MESSAGE -> {
                    execute(sendAction(sender.getChatId(), TYPING));
                    execute(executorService.edit(sender));
                }
                case SEND_PHOTO -> {
                    execute(sendAction(sender.getChatId(), UPLOADPHOTO));
                    execute(executorService.sendPhoto(sender));
                }
                case SEND_LOCATION -> {
                    execute(sendAction(sender.getChatId(), FINDLOCATION));
                    execute(executorService.sendLocation(sender));
                }
                case SEND_CONTACT -> {
                    execute(sendAction(sender.getChatId(), TYPING));
                    execute(executorService.sendContact(sender));
                }
                case SEND_DOCUMENT -> {
                    execute(sendAction(sender.getChatId(), UPLOADDOCUMENT));
                    execute(executorService.sendDocument(sender));
                }
            }
        } catch (TelegramApiException e) {
            log.warn("Bot is not working " + e);
        }
    }

    private SendChatAction sendAction(Long chatId, ActionType type) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setAction(type);
        sendChatAction.setChatId(chatId);
        return sendChatAction;
    }
}
