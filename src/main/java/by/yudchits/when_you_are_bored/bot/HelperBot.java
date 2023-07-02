package by.yudchits.when_you_are_bored.bot;

import by.yudchits.when_you_are_bored.configuration.BotConfig;
import by.yudchits.when_you_are_bored.service.FindActivityService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class HelperBot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig config;

    @Autowired
    private FindActivityService findActivityService;

    private static final String HELP_TEXT = """
            You can use these commands:
                        
            /activity - get a random activity
            /help = get list of the commands
            """;

    private static final String[] types = {"education", "recreational", "social", "diy", "charity", "cooking", "relaxation", "music", "busywork"};
    private static final String[] idOfTypes = {"EDUCATION_BUTTON", "RECREATIONAL_BUTTON", "SOCIAL_BUTTON", "DIY_BUTTON", "CHARITY_BUTTON", "COOKING_BUTTON", "RELAXATION_BUTTON", "MUSIC_BUTTON", "BUSYWORK_BUTTON"};

    public HelperBot(@Value("${bot.token}") String botToken) {
        super(botToken);

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/activity", "get an activity"));
        commands.add(new BotCommand("/help", "get list of commands"));

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Menu error: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getUserName();
            String message = update.getMessage().getText();

            switch (message) {
                case "/start" -> startCommand(chatId, userName);
                case "/activity" -> getActivityCommands(chatId);
                case "/activity_by_type" -> getActivityByTypeCommand(chatId);
                case "/help" -> sendMessage(chatId, HELP_TEXT);
                default -> sendMessage(chatId, "This command is not supported!");
            }
        } else if (update.hasCallbackQuery()) {
            
        }
    }

    private void startCommand(long chatId, String userName) {
        String text = EmojiParser.parseToUnicode("""
                Welcome, %s!:fire:
                                
                We can help you to find a thing that you can do when you're bored
                """);

        String formattedText = String.format(text, userName);

        sendMessage(chatId, formattedText);
        log.info("The beginning of using of the bot by " + userName);
    }

    private void getActivityCommands(long chatId) {
        String activity = findActivityService.getActivity();

        sendMessage(chatId, activity);
    }

    private void getActivityByTypeCommand(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose a type of activity:");
        message.setReplyMarkup(getInlineKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Occurred error: " + e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        message.setReplyMarkup(getReplyKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Occurred error: " + e.getMessage());
        }
    }

    private ReplyKeyboardMarkup getReplyKeyboard(){
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("/activity");

        rows.add(row);

        row = new KeyboardRow();
        row.add("/activity_by_type");
        rows.add(row);

        keyboard.setKeyboard(rows);

        return keyboard;
    }

    private InlineKeyboardMarkup getInlineKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        int counter = 0;
        for (int i = 0; i < types.length; i++) {
            if(counter == 3){
                rows.add(row);
                row = new ArrayList<>();
                counter = 0;
            }

            var button = new InlineKeyboardButton();
            button.setText(types[i]);
            button.setCallbackData(idOfTypes[i]);

            row.add(button);
            counter++;
        }
        if(counter > 0)
            rows.add(row);

        keyboard.setKeyboard(rows);

        return keyboard;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
