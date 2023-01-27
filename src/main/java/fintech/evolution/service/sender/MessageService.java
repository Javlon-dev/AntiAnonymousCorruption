package fintech.evolution.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import fintech.evolution.service.user.AbstractService;
import fintech.evolution.variable.constants.region.*;
import fintech.evolution.variable.constants.xarid.StateConstants;
import fintech.evolution.variable.entity.UserReview;
import fintech.evolution.variable.entity.user.UserDebate;
import fintech.evolution.variable.message.GeneralSender;
import fintech.evolution.variable.message.SenderDocument;
import fintech.evolution.variable.message.SenderMessage;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import static fintech.evolution.variable.constants.region.RegionConstant.*;
import static fintech.evolution.variable.constants.user.UserLang.LANG_RU;
import static fintech.evolution.variable.constants.user.UserLang.LANG_UZ;
import static fintech.evolution.variable.constants.user.UserMenu.*;
import static fintech.evolution.variable.constants.user.UserStep.*;
import static fintech.evolution.variable.constants.xarid.StateConstants.*;

@Service
@Slf4j
public class MessageService extends AbstractService {
    @Value("${admin.chatId:0}")
    private String adminChatId;

    public GeneralSender start(Long chatId, Message message) {
        if (message.getText().equals("/start")) service.setStep(chatId, STEP_START);
        String step = service.getStep(chatId);
        String lang = service.getLang(chatId);
        if (isValidMessage(message.getText())) {
            switch (step) {
                case STEP_START -> {
                    String msg = lang.equals(LANG_UZ) ? "*Керакли бўлимни танланг!*" : "*Выберите нужный раздел!*";
                    return stepStart(chatId, msg);
                }
                case STEP_WRITE_REVIEW -> {
                    return stepWriteReview(chatId, message.getText());
                }
                case STEP_REGISTRATION -> {
                    return setNameFunction(chatId, message.getText());
                }
                case STEP_SET_SURNAME -> {

                    service.setStep(chatId, STEP_SET_PHONE);
                    return setPhoneMenu(chatId, message.getText());
                }
                case STEP_SET_PHONE -> {
                    String contact = message.getText();
                    boolean c = isFormalContact(contact);
                    if (!c) {
                        boolean b = service.getLang(chatId).equals(LANG_UZ);
                        String ms = b ? """
                                <b>Телефон рақам хато киритилди!
                                Текшириб қайтадан киритинг!
                                Мисол учун: +998901234567</b>
                                """ : """
                                <b>Номер телефона был введен неверно!
                                Пожалуйста проверьте и попробуйте снова!
                                Например: +998901234567</b>
                                """;
                        return SenderMessage.builder().text(ms).chatId(chatId).reply(getKeyboardWithRequestContact(chatId, b ? MENU_SHARE_CONTACT_UZ : MENU_SHARE_CONTACT_RU)).parseMode(ParseMode.HTML).build();
                    }


                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    String ms = b ? "<b>E-mail манзилингизни киритинг!</b>" : "<b>Введите адрес электронной почты!</b>";

                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setPhoneNumber(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_EMAIL);


                    return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(ms).build();
                }
                case STEP_SET_EMAIL -> {
                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    boolean c = isFormalEmail(message.getText());
                    if (!c) {
                        String ms = b ? """
                                <b>E-mail манзили хато киритилди!</b>
                                <b>Илтимос текшириб қайтадан киритинг!</b>
                                """ : """
                                <b>Адрес электронной почты был введен неправильно!</b>
                                <b>Пожалуйста проверьте и попробуйте снова!</b>
                                """;

                        return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(ms).build();
                    }
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setEmail(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_SUBJECT_NAME);
                    String msg = b ? "<b>Тадбиркорлик субъекти номини киритинг!</b>" : "<b>Введите название юридического лица!</b>";

                    return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();

                }
                case STEP_SET_SUBJECT_NAME -> {
                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    if (message.getText().length() < 2) {
                        String msg = b ? "<b>Тадбиркорлик субъекти номини киритинг!</b>" : "<b>Введите название юридического лица!</b>";

                        return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setSubjectName(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_REGION);

                    String msg = lang.equals(LANG_UZ) ? "<b>Вилоятни танланг!</b>" : "<b>Выберите регион!</b>";
                    ReplyKeyboardMarkup keyboard = getRegionKeyboard(lang);
                    return SenderMessage.builder().reply(keyboard).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                }
                case STEP_SET_REGION -> {
                    lang = service.getLang(chatId);
                    boolean b = lang.equals(LANG_UZ);
                    if (isValidRegion(message.getText())) {
                        ReplyKeyboardMarkup keyboard = getRegionKeyboard(lang);
                        return SenderMessage.builder().chatId(chatId).text(b ? "<b>Вилоятни танланг!</b>" : "<b>Выберите регион!</b>").reply(keyboard).parseMode(ParseMode.HTML).build();
                    }
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setRegion(message.getText());
                    service.setUserRegion(chatId, message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_DISTRICT);
                    String msg = lang.equals(LANG_UZ) ? "<b>Шаҳар/Туман</b>ни танланг!" : "Выберите <b>город/района!</b>";
                    ReplyKeyboardMarkup keyboard = getDistrictKeyboard(lang, message.getText());

                    return SenderMessage.builder().reply(keyboard).chatId(chatId).text(msg).parseMode(ParseMode.HTML).build();
                }
                case STEP_SET_DISTRICT -> {
                    boolean b = lang.equals(LANG_UZ);
                    String text = message.getText();
                    String region = service.getUserRegion(chatId);
                    boolean check = isDistrict(text, region);
                    if (!check) {
                        String msg = b ? "<b>Шаҳар/Туман</b>ни танланг!" : "Выберите <b>город/района!</b>";
                        ReplyKeyboardMarkup keyboard = getDistrictKeyboard(lang, region);

                        return SenderMessage.builder().reply(keyboard).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setDistrict(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_STIR);
                    String msg = b ? "<b>СТИР рақамини киритинг!</b>" : "<b>Введите номер СТИР!</b>";
                    return SenderMessage.builder().reply(getKeyboardWithRequestContact(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                }
                case STEP_SET_STIR -> {
                    UserDebate userDebate = service.getUserDebate(chatId);

                    try {
                        if (message.getText().length() != 9) {
                            throw new RuntimeException();
                        }
                        boolean b = service.getLang(chatId).equals(LANG_UZ);
                        Long stir = Long.valueOf(message.getText());
                        userDebate.setStir(stir);
                        service.setUserDebate(chatId, userDebate);
                        service.setStep(chatId, STEP_SET_TYPE);
                        String msg = b ? """
                                <b>Фаолиятингиз турини киритинг!</b>
                                <b>Мисол учун: </b> чакана савдо
                                """ : """
                                <b>Введите род деятельности!</b>
                                <b>Например:</b> чакана савдо
                                """;
                        return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    } catch (Exception e) {
                        String msg = lang.equals(LANG_UZ) ? "<b>СТИР рақам хато текшириб қайтадан киритинг!</b>" : "<b>Номер СТИР введен неверно, проверьте и введите еще раз!</b>";
                        return SenderMessage.builder().reply(getKeyboardWithRequestContact(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }

                }
                case STEP_SET_TYPE -> {
                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setServiceType(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_PRODUCT_NAME);
                    String msg = b ? "<b>Товар (иш, хизмат) номини киритинг!</b>" : "<b>Введите наименование товара (работы, услуги)!</b>";
                    return SenderMessage.builder().reply(getKeyboardWithRequestContact(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                }
                case STEP_SET_PRODUCT_NAME -> {
                    if (message.getText().length() < 2) {
                        String msg = lang.equals(LANG_UZ) ? "<b>Товар (иш, хизмат) номини киритинг!</b>" : "<b>Введите наименование товара (работы, услуги)!</b>";
                        return SenderMessage.builder().reply(getKeyboardWithRequestContact(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setProductName(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_ACTIVITY_YEAR);
                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    String msg = b ? """
                            <b>Давлат харидларидаги фоалиятингизни нечинчи йилда бошлагансиз?</b>
                            <b>Йилни киритинг! Мисол учун : 2022</b>
                            """ : """
                            <b>В каком году вы начали свою карьеру в Государственных закупок?</b>
                            <b>Введите год! Например: 2022</b>
                            """;

                    return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                }
                case STEP_SET_ACTIVITY_YEAR -> {
                    try {

                        if (message.getText().length() != 4) {
                            throw new RuntimeException();
                        }
                        int year = Integer.parseInt(message.getText());
                        LocalDateTime time = LocalDateTime.now();
                        int now = time.getYear();
                        if (year > now || year < 1990) {
                            throw new RuntimeException();
                        }
                        UserDebate userDebate = service.getUserDebate(chatId);
                        userDebate.setActivityYear(year);
                        service.setUserDebate(chatId, userDebate);
                        service.setStep(chatId, STEP_SET_OPERATOR_NAME);
                        boolean b = service.getLang(chatId).equals(LANG_UZ);

                        String msg = b ? """
                                <b>Давлат харидларидан рўйхатдан ўтган операторингиз номини танланг!</b>
                                """ : """
                                <b>Выберите имя вашего зарегистрированного оператора из Государственных закупок!</b>
                                """;
                        KeyboardRow r1 = reply.getRows(OPERATOR_XT_XARID);
                        KeyboardRow r2 = reply.getRows(OPERATOR_XOM_ASHYO);
                        KeyboardRow r3 = reply.getRows(OPERATOR_RAQAMLI_MARKAZ);
                        KeyboardRow r4 = reply.getRows(OPERATOR_QURILISH_AXBOROT);
                        KeyboardRow r5 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
                        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2, r3, r4, r5);
                        return SenderMessage.builder().reply(keyboard).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();

                    } catch (Exception e) {
                        boolean b = service.getLang(chatId).equals(LANG_UZ);
                        String msg = b ? """
                                <b>Давлат харидларидаги фоалиятингизни нечинчи йилда бошлагансиз?</b>
                                <b>Йилни киритинг! Мисол учун : 2022</b>
                                """ : """
                                <b>В каком году вы начали свою карьеру в Государственных закупок?</b>
                                <b>Введите год! Например: 2022</b>
                                """;
                        return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }
                }
                case STEP_SET_OPERATOR_NAME -> {
                    boolean formative = isFormative(StateConstants.class, message.getText());
                    if (!formative) {
                        boolean b = service.getLang(chatId).equals(LANG_UZ);

                        String msg = b ? """
                                <b>Давлат харидларидан рўйхатдан ўтган операторингиз номини танланг!</b>
                                """ : """
                                <b>Выберите имя вашего зарегистрированного оператора из Государственных закупок!</b>
                                """;
                        KeyboardRow r1 = reply.getRows(OPERATOR_XT_XARID);
                        KeyboardRow r2 = reply.getRows(OPERATOR_XOM_ASHYO);
                        KeyboardRow r3 = reply.getRows(OPERATOR_RAQAMLI_MARKAZ);
                        KeyboardRow r4 = reply.getRows(OPERATOR_QURILISH_AXBOROT);
                        KeyboardRow r5 = reply.getRows(b ? MENU_CANCEL_UZ : MENU_CANCEL_RU);
                        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2, r3, r4, r5);
                        return SenderMessage.builder().reply(keyboard).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }

                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setOperatorName(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    service.setStep(chatId, STEP_SET_DATE);
                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    String msg = b ? """
                            <b>Давлат харидлари электрон тизимидан рўйхатдан ўтган йилингизни киритинг!</b>
                            <b>Мисол учун : 2022</b>
                            """ : """
                            <b>Введите свой год регистрации в электронной системе Государственных закупок!</b>
                            <b>Например: 2022</b>
                            """;
                    return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();

                }
                case STEP_SET_DATE -> {
                    try {
                        if (message.getText().length() != 4) {
                            throw new RuntimeException();
                        }
                        int year = Integer.parseInt(message.getText());
                        LocalDateTime time = LocalDateTime.now();
                        int now = time.getYear();
                        if (year > now || year < 2011) {
                            throw new RuntimeException();
                        }
                        UserDebate userDebate = service.getUserDebate(chatId);
                        userDebate.setRegisteredYear(year);
                        service.setUserDebate(chatId, userDebate);
                        service.setStep(chatId, STEP_SET_ADDRESS);
                        boolean b = service.getLang(chatId).equals(LANG_UZ);

                        String msg = b ? """
                                <b>Давлат харидлари электрон тизимидан рўйхатдан ўтган ҳудудингизни танланг!</b>
                                """ : """
                                <b>Выберите регион регистрации в электронной системе Государственных закупок!</b>
                                """;
                        ReplyKeyboardMarkup keyboard = getRegionKeyboard(service.getLang(chatId));
                        return SenderMessage.builder().reply(keyboard).chatId(chatId).text(msg).parseMode(ParseMode.HTML).build();

                    } catch (Exception e) {
                        boolean b = service.getLang(chatId).equals(LANG_UZ);
                        String msg = b ? """
                                <b>Давлат харидлари электрон тизимидан рўйхатдан ўтган йилингизни киритинг!</b>
                                <b>Мисол учун : 2022</b>
                                """ : """
                                <b>Введите свой год регистрации в электронной системе Государственных закупок!</b>
                                <b>Например: 2022</b>
                                """;
                        return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }
                }
                case STEP_SET_ADDRESS -> {
                    boolean formative = isFormative(RegionConstant.class, message.getText());
                    boolean b = service.getLang(chatId).equals(LANG_UZ);
                    if (!formative) {

                        String msg = b ? """
                                <b>Давлат харидлари электрон тизимидан рўйхатдан ўтган ҳудудингизни танланг!</b>
                                """ : """
                                <b>Выберите регион регистрации в электронной системе Государственных закупок!</b>
                                """;
                        ReplyKeyboardMarkup keyboard = getRegionKeyboard(service.getLang(chatId));
                        return SenderMessage.builder().reply(keyboard).chatId(chatId).parseMode(ParseMode.HTML).text(msg).build();
                    }
                    UserDebate userDebate = service.getUserDebate(chatId);
                    userDebate.setArea(message.getText());
                    service.setUserDebate(chatId, userDebate);
                    String uz = """
                            *Киритган маълумотларингиз тўғрилигини текширинг!*
                                                        
                            *Тадбиркорлик субъектининг номи : *  ``` %s ```
                                                        
                                        *Жойлашган ҳудуди*
                                        
                            *Вилоят : *  ``` %s ```
                            *Шаҳар/туман : *  ``` %s ```
                                                        
                            *СТИР рақами : *  ``` %s ```
                            *Фаолият тури : *  ``` %s ```
                            *Товар (иш, хизмат) номи : *  ``` %s ```
                            *Телефон рақам : *  ``` %s ```
                            *Е-mail : *  ``` %s ```
                                                                                    
                                        *Давлат харидларидан рўйхатдан ўтганлиги*
                                                        
                            *Оператор номи : *  ``` %s ```
                            *Рўйхатдан ўтган йили : *  ``` %s ```
                            *Рўйхатдан ўтган ҳудуди : *  ``` %s ```
                                                        
                            """;
                    service.setStep(chatId, STEP_SET_FINISH);
                    String ru = """
                            *Проверьте правильность введенных вами сообщений!*
                                                        
                            *Название хозяйствующего субъекта : *  ``` %s ```
                                                        
                                        *Район локации*
                                        
                            *Регион : *  ``` %s ```
                            *Город/район : *  ``` %s ```
                                                        
                            *Номер СТИР : *  ``` %s ```
                            *Тип активности : *  ``` %s ```
                            *Название продукта (работы, услуги) : *  ``` %s ```
                            *Номер телефона : *  ``` %s ```
                            *Е-mail : *  ``` %s ```

                                        *Регистрация государственных закупок*
                                                        
                            *Имя оператора : *  ``` %s ```
                            *В год регистрации : *  ``` %s ```
                            *Зарегистрированная площадь : *  ``` %s ```
                                                        
                            """;

                    String phoneNumber = userDebate.getPhoneNumber();
                    String email = userDebate.getEmail();
                    String subjectName = userDebate.getSubjectName();
                    String region = userDebate.getRegion();
                    String district = userDebate.getDistrict();
                    String serviceType = userDebate.getServiceType();
                    String productName = userDebate.getProductName();
                    Long stir = userDebate.getStir();
                    String operatorName = userDebate.getOperatorName();
                    Integer registeredYear = userDebate.getRegisteredYear();
                    String area = userDebate.getArea();

                    String msg = String.format(b ? uz : ru, subjectName, region, district, stir.toString(), serviceType, productName, phoneNumber, email, operatorName, registeredYear, area);
                    String key = b ? MENU_CONFIRM_UZ : MENU_CONFIRM_RU;
                    return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, key)).chatId(chatId).text(msg).build();
                }
            }
        } else {
            switch (message.getText()) {
                case MENU_REGISTRATION_UZ, MENU_REGISTRATION_RU -> {
                    if (step.equals(STEP_START)) {

                        service.setStep(chatId, STEP_REGISTRATION);
                        return getRegistration(chatId);
                    } else {
                        return null;
                    }
                }
                case MENU_CHANGE_LANG_UZ, MENU_CHANGE_LANG_RU -> {
                    if (step.equals(STEP_START)) {
                        service.setStep(chatId, STEP_SELECTED_LANG);
                        return changeLangMenu(chatId, null);
                    } else {
                        return null;
                    }
                }
                case MENU_REVIEW_UZ, MENU_REVIEW_RU -> {
                    if (chatId.equals(Long.valueOf(adminChatId))) {
                        LocalDate date = LocalDate.now();
                        String time = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        return SenderDocument.builder().chatId(Long.valueOf(adminChatId)).caption(time + " ҳолатига кўра.").inputFile(getInputBot()).build();
                    }
                    if (step.equals(STEP_START)) {
                        service.setStep(chatId, STEP_REVIEW);
                        return reviewMenu(chatId);
                    } else {
                        return null;
                    }
                }
                case MENU_SELECTED_LANG_UZ, MENU_SELECTED_LANG_RU -> {

                    if (step.equals(STEP_SELECTED_LANG)) {
                        if (message.getText().equals(MENU_SELECTED_LANG_UZ)) {
                            service.setLang(chatId, LANG_UZ);
                        } else {
                            service.setLang(chatId, LANG_RU);
                        }
                        lang = service.getLang(chatId);
                        String msg = lang.equals(LANG_UZ) ? "Ўзбек тили танланди." : lang.equals(LANG_RU) ? "Выбран русский язык." : "";
                        return changeLangMenu(chatId, msg);
                    } else {
                        return null;
                    }
                }
                case MENU_BACK_UZ, MENU_BACK_RU -> {
                    service.setStep(chatId, STEP_START);
                    String msg = lang.equals(LANG_UZ) ? "*Керакли бўлимни танланг!*" : "*Выберите нужный раздел!*";
                    return stepStart(chatId, msg);
                }
                case MENU_CANCEL_UZ, MENU_CANCEL_RU -> {
                    return menuCancel(chatId);
                }
                case MENU_CONFIRM_UZ, MENU_CONFIRM_RU -> {
                    if (step.equals(STEP_SET_FINISH)) {
                        service.setStep(chatId, STEP_START);
                        UserDebate userDebate = service.getUserDebate(chatId);
                        try {
                            String msg = lang.equals(LANG_UZ) ? "Аризангиз қабул қилинди!" : "Ваша заявка принята!";
                            Optional<UserDebate> debate = userDebateRepository.findByStir(userDebate.getStir());
                            if (debate.isPresent()) {
                                Long id = debate.get().getId();
                                userDebate.setId(id);
                                userDebate.setCreatedAt(LocalDateTime.now());
                            }
                            userDebateRepository.save(userDebate);
                            return stepStart(chatId, msg);
                        } catch (Exception e) {
                            String msg = lang.equals(LANG_UZ) ? """
                                    Аризангиз қабул қилинмади!
                                    Илтимос қайтадан уриниб кўринг!
                                    """ : """
                                    Ваша заявка не принята!
                                    Пожалуйста, попробуйте еще раз!
                                    """;
                            return stepStart(chatId, msg);
                        }
                    }
                    return null;
                }
            }
        }


        return null;
    }

    private GeneralSender getRegistration(Long chatId) {
        boolean b = service.getLang(chatId).equals(LANG_UZ);
        String ms = b ? "<b>Исмингизни киритинг!</b>" : "<b>Введите ваше имя!</b>";

        return SenderMessage
                .builder()
                .reply(getKeyboardWithCancel(chatId, null))
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .text(ms).build();
    }

    private GeneralSender setNameFunction(Long chatId, String text) {
        UserDebate userDebate = service.getUserDebate(chatId);
        userDebate.setName(text);
        service.setUserDebate(chatId, userDebate);
        service.setStep(chatId, STEP_SET_SURNAME);

        boolean b = service.getLang(chatId).equals(LANG_UZ);
        String ms = b ? "<b>Фамилиянгизни киритинг!</b>" : "<b>Введите свою фамилию!</b>";


        return SenderMessage
                .builder()
                .reply(getKeyboardWithCancel(chatId, null))
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .text(ms).build();
    }

    private boolean isDistrict(String text, String region) {
        switch (region) {
            case TASHKENT -> {
                return isFormative(TashkentConstants.class, text);
            }
            case TASHKENT_REGION -> {
                return isFormative(TashkentRegionConstants.class, text);
            }
            case ANDIJAN_REGION -> {
                return isFormative(AndijanConstants.class, text);
            }
            case BUKHARA_REGION -> {
                return isFormative(BuxoroConstants.class, text);
            }
            case JIZZAKH_REGION -> {
                return isFormative(JizzakhConstants.class, text);
            }
            case KASHKADARYA_REGION -> {
                return isFormative(KashkadaryaConstants.class, text);
            }
            case NAVOIY_REGION -> {
                return isFormative(NavoiyConstants.class, text);
            }
            case NAMANGAN_REGION -> {
                return isFormative(NamanganConstants.class, text);
            }
            case SAMARKAND_REGION -> {
                return isFormative(SamarkandConstants.class, text);
            }
            case SURKHANDARYA_REGION -> {
                return isFormative(SurkhandaryaConstants.class, text);
            }
            case SIRDARYA_REGION -> {
                return isFormative(SirdaryaConstants.class, text);
            }
            case FERGANA_REGION -> {
                return isFormative(FerganaConstants.class, text);
            }
            case KHOREZM_REGION -> {
                return isFormative(KhorezmConstants.class, text);
            }
            case KARAKALPAKISTAN_REPUBLIC -> {
                return isFormative(KarakalpakistanConstants.class, text);
            }
        }

        return false;
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

    private boolean isFormalEmail(String email) {
        String mail = "^[_A-Za-z\\d-+]+(.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(.[A-Za-z\\d]+)*(.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(mail);
        return pattern.matcher(email).matches();
    }

    private boolean isFormalContact(String contact) {
        String phone = "\\+\\d{12}";
        Pattern pattern = Pattern.compile(phone);
        return pattern.matcher(contact).matches();
    }


    private GeneralSender menuCancel(Long chatId) {
        String lang = service.getLang(chatId);
        String step = service.getStep(chatId);
        if (step.equals(STEP_START) || step.equals(STEP_SELECTED_LANG)) {
            return null;
        }
        service.setUserReview(chatId, null);
        service.setUserDebate(chatId, null);
        String msg = lang.equals(LANG_UZ) ? "*Керакли бўлимни танланг!*" : "*Выберите нужный раздел!*";
        service.setStep(chatId, STEP_START);

        return stepStart(chatId, msg);
    }

    private GeneralSender stepWriteReview(Long chatId, String text) {
        UserReview userReview = service.getUserReview(chatId);
        userReview.setReview(text);
        String lang = service.getLang(chatId);
        try {
            String msg = lang.equals(LANG_UZ) ? "Раҳмат, изоҳингиз қабул қилинди!" : "Спасибо, Ваш отзыв принят!";
            Optional<UserReview> entity = userReviewRepository.findByPhoneNumber(userReview.getPhoneNumber());

            entity.ifPresent(review -> {
                userReview.setId(review.getId());
                userReview.setCreatedAt(LocalDateTime.now());
            });

            userReviewRepository.save(userReview);
            service.setUserReview(chatId, null);
            service.setStep(chatId, STEP_START);
            return stepStart(chatId, msg);
        } catch (Exception exception) {
            String msg = lang.equals(LANG_UZ) ? "Изоҳингиз 255 та белгидан ошмаслиги керак!" : "Ваш комментарий не может превышать 255 символов!";

            return SenderMessage.builder().chatId(chatId).text(msg).reply(getKeyboardWithRequestContact(chatId, null)).build();
        }
    }

    private GeneralSender reviewMenu(Long chatId) {
        String lang = service.getLang(chatId);

        ReplyKeyboardMarkup keyboard = getKeyboardWithRequestContact(chatId, lang.equals(LANG_UZ) ? MENU_SHARE_CONTACT_UZ : MENU_SHARE_CONTACT_RU);
        String msg = lang.equals(LANG_UZ) ? "Изоҳ қолдиришдан олдин телефон рақамингизни жўнатинг:" : "Перед тем, как оставить отзыв, отправьте свой контакт:";


        return SenderMessage.builder().text(msg).chatId(chatId).reply(keyboard).parseMode(ParseMode.HTML).build();

    }

    private GeneralSender changeLangMenu(Long chatId, String text) {
        String lang = service.getLang(chatId);
        String back = lang.equals(LANG_UZ) ? MENU_BACK_UZ : MENU_BACK_RU;

        KeyboardButton btnUz = reply.getButton(MENU_SELECTED_LANG_UZ);
        KeyboardButton btnRu = reply.getButton(MENU_SELECTED_LANG_RU);
        KeyboardButton btnBack = reply.getButton(back);
        KeyboardRow r1 = reply.getRows(btnUz, btnRu);
        KeyboardRow r2 = reply.getRows(btnBack);
        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2);
        String msg = text != null ? text : (lang.equals(LANG_UZ) ? "Тилни танланг!" : "Выберите свой язык!");

        return SenderMessage.builder().text(msg).reply(keyboard).chatId(chatId).parseMode(ParseMode.HTML).build();
    }

    private GeneralSender setPhoneMenu(Long chatId, String text) {
        UserDebate userDebate = service.getUserDebate(chatId);
        userDebate.setSurname(text);
        service.setUserDebate(chatId, userDebate);

        boolean b = service.getLang(chatId).equals(LANG_UZ);
        String ms = b ? """
                Рўйхатдан ўтиш учун телефон рақамингизни киритинг!
                Мисол учун: +998901234567
                """ : """
                Введите свой номер телефона для регистрации!
                Например: +998901234567
                """;


        return SenderMessage.builder().text(ms).chatId(chatId).reply(getKeyboardWithRequestContact(chatId, b ? MENU_SHARE_CONTACT_UZ : MENU_SHARE_CONTACT_RU)).parseMode(ParseMode.HTML).build();
    }

    public GeneralSender getContact(Long chatId, String phoneNumber) {
        UserDebate userDebate = service.getUserDebate(chatId);
        userDebate.setPhoneNumber(phoneNumber);
        service.setUserDebate(chatId, userDebate);
        service.setStep(chatId, STEP_SET_EMAIL);
        boolean b = service.getLang(chatId).equals(LANG_UZ);
        String ms = b ? "E-mail манзилингизни киритинг!" : "Введите адрес электронной почты!";
        return SenderMessage.builder().reply(getKeyboardWithCancel(chatId, null)).chatId(chatId).text(ms).build();
    }

    private GeneralSender stepStart(Long chatId, String msg) {
        String lang = service.getLang(chatId);
        String registration = lang.equals(LANG_UZ) ? MENU_REGISTRATION_UZ : MENU_REGISTRATION_RU;
        String changeLang = lang.equals(LANG_UZ) ? MENU_CHANGE_LANG_UZ : MENU_CHANGE_LANG_RU;
        String contact = lang.equals(LANG_UZ) ? MENU_REVIEW_UZ : MENU_REVIEW_RU;
        KeyboardButton b1 = reply.getButton(registration);
        KeyboardButton b2 = reply.getButton(contact);
        KeyboardButton b3 = reply.getButton(changeLang);
        KeyboardRow r1 = reply.getRows(b1);
        KeyboardRow r2 = reply.getRows(b2, b3);
        ReplyKeyboardMarkup keyboard = reply.getKeyboard(r1, r2);


        return SenderMessage.builder().text(msg).chatId(chatId).reply(keyboard).build();
    }

    public GeneralSender messageContact(Long chatId, Contact contact) {
        String lang = service.getLang(chatId);
        service.setStep(chatId, STEP_WRITE_REVIEW);
        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        String phoneNumber = contact.getPhoneNumber();

        UserReview entity = service.getUserReview(chatId);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setPhoneNumber(phoneNumber);
        service.setUserReview(chatId, entity);

        String msg = lang.equals(LANG_UZ) ? """
                Илимос, бизга изоҳ/истак/шикоятингизни ёзиб жўнатинг:

                (1-256 белги)""" : """
                Пожалуйста, напишите и отправьте нам свой отзыв/пожелание/жалобу:
                                
                (1-256 символов)
                """;

        return SenderMessage.builder().text(msg).chatId(chatId).parseMode(ParseMode.HTML).reply(getKeyboardWithRequestContact(chatId, null)).build();
    }

    protected boolean isValidRegion(String text) {

        Field[] declaredFields = RegionConstant.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                if (text.equals(declaredField.get(declaredField.getName()).toString())) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                log.warn("<< UserMenu : " + e.getMessage());
            }
        }
        return true;
    }


}
