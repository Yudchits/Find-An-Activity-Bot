package by.yudchits.when_you_are_bored.bot;

import by.yudchits.when_you_are_bored.configuration.BotConfig;
import by.yudchits.when_you_are_bored.service.FindActivityService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelperBot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig config;

    @Autowired
    private FindActivityService findActivityService;

    private static final String HELP_TEXT = """
            You can use these commands:
            
            /get_activity - get a random activity
            /help = get list of the commands
            """;

    public HelperBot(@Value("${bot.token}") String botToken){
        super(botToken);

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/get_activity", "get an activity"));
        commands.add(new BotCommand("/help", "get list of commands"));

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getUserName();
            String message = update.getMessage().getText();

            switch (message){
                case "/start" -> startCommand(chatId, userName);
                case "/get_activity" -> getActivityCommands(chatId);
                case "/help" -> sendMessage(chatId, HELP_TEXT);
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

    private void getActivityCommands(long chatId){
        String activity = findActivityService.getActivity();

        sendMessage(chatId, activity);
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
