package by.yudchits.when_you_are_bored.configuration;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient();
    }
}
