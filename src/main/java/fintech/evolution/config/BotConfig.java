package fintech.evolution.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("/application.properties")
public class BotConfig {

    @Value("${bot.name:noName}")
    private String botName;

    @Value("${bot.token:noToken}")
    private String botToken;

}
