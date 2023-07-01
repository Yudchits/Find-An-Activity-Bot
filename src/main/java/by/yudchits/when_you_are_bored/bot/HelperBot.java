package by.yudchits.when_you_are_bored.bot;

import by.yudchits.when_you_are_bored.configuration.BotConfig;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelperBot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig config;

    public HelperBot(@Value("${bot.token}") String botToken){
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getUserName();
            String message = update.getMessage().getText();

            switch (message){
                case "/start" -> startCommand(chatId, userName);
                default -> sendMessage(chatId, "This command is not supported!");
            }
        }
    }

    private void startCommand(long chatId, String userName) {
        String text = EmojiParser.parseToUnicode("""
                Welcome, %s!:fire:
                
                We can help you to find a thing that you can do when you're bored
                """);

        String formattedText = String.format(text, userName);

        sendMessage(chatId, formattedText);
    }

    private void sendMessage(long chatId, String formattedText) {
        SendMessage message = new SendMessage(String.valueOf(chatId), formattedText);

        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
