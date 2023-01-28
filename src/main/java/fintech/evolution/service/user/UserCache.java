package fintech.evolution.service.user;

import fintech.evolution.variable.entity.UserCooperation;
import fintech.evolution.variable.entity.user.UserDebate;

public interface UserCache {
    void setStep(Long chatId, String step);

    String getStep(Long chatId);

    void setLang(Long chatId, String lang);

    String getLang(Long chatId);

    void setMessageId(Long chatId, Integer messageId);

    Integer getMessageId(Long chatId);

    void setUserReview(Long chatId, UserCooperation userCooperation);

    UserCooperation getUserReview(Long chatId);

    void setUserDebate(Long chatId, UserDebate userDebate);

    UserDebate getUserDebate(Long chatId);

    void setUserRegion(Long chatId, String region);

    String getUserRegion(Long chatId);


}
