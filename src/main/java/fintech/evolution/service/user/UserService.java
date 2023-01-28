package fintech.evolution.service.user;

import org.springframework.stereotype.Component;
import fintech.evolution.variable.entity.UserCooperation;
import fintech.evolution.variable.entity.user.UserDebate;

import java.util.HashMap;
import java.util.Map;

import static fintech.evolution.variable.constants.user.UserLang.*;
import static fintech.evolution.variable.constants.user.UserStep.*;

@Component
public class UserService implements UserCache {
    private final Map<Long, String> mapStep = new HashMap<>();
    private final Map<Long, String> mapLang = new HashMap<>();
    private final Map<Long, Integer> mapMessageId = new HashMap<>();
    private final Map<Long, String> mapRegion = new HashMap<>();
    private final Map<Long, UserCooperation> mapUserCooperation = new HashMap<>();
    private final Map<Long, UserDebate> mapUserDebate = new HashMap<>();


    @Override
    public void setStep(Long chatId, String step) {
        mapStep.put(chatId, step);
    }

    @Override
    public void setLang(Long chatId, String lang) {
        mapLang.put(chatId, lang);
    }

    @Override
    public void setMessageId(Long chatId, Integer messageId) {
        mapMessageId.put(chatId, messageId);
    }

    @Override
    public String getStep(Long chatId) {
        mapStep.putIfAbsent(chatId, STEP_START);
        return mapStep.get(chatId);
    }

    @Override
    public String getLang(Long chatId) {
        mapLang.putIfAbsent(chatId, LANG_UZ);
        return mapLang.get(chatId);
    }

    @Override
    public Integer getMessageId(Long chatId) {
        return mapMessageId.get(chatId);
    }

    @Override
    public void setUserReview(Long chatId, UserCooperation userCooperation) {
        mapUserCooperation.put(chatId, userCooperation);
    }

    @Override
    public UserCooperation getUserReview(Long chatId) {
        mapUserCooperation.putIfAbsent(chatId, new UserCooperation());
        return mapUserCooperation.get(chatId);
    }

    @Override
    public void setUserDebate(Long chatId, UserDebate userDebate) {
        mapUserDebate.put(chatId, userDebate);
    }

    @Override
    public UserDebate getUserDebate(Long chatId) {
        mapUserDebate.putIfAbsent(chatId, new UserDebate());
        return mapUserDebate.get(chatId);
    }

    @Override
    public void setUserRegion(Long chatId, String region) {
        mapRegion.put(chatId, region);
    }

    @Override
    public String getUserRegion(Long chatId) {
        return mapRegion.get(chatId);
    }


}
