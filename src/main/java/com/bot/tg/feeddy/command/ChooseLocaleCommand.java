package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.bot.tg.feeddy.command.Emoji.ENG_FLAG;
import static com.bot.tg.feeddy.command.Emoji.RU_FLAG;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ChooseLocaleCommand implements Command {
    public static final String START = "/start";
    public static final String CHOOSE_LANGUAGE = "Choose language.\nВыберите язык.";
    private final UserRepository repository;

    @Override
    public SendMessage execute(Update update) {
        saveUser(update);
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(CHOOSE_LANGUAGE)
                .setReplyMarkup(createKeyboard());
    }

    private void saveUser(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (!repository.existsByChatId(chatId)) {
            User user = new User();
            user.setChatId(chatId);
            user.setName(update.getMessage().getChat().getUserName());
            repository.save(user);
        }
    }

    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton ruButton = new InlineKeyboardButton()
                .setText(RU_FLAG.getValue())
                .setCallbackData(RU_FLAG.getName());

        InlineKeyboardButton engButton = new InlineKeyboardButton()
                .setText(ENG_FLAG.getValue())
                .setCallbackData(ENG_FLAG.getName());

        keyboard.setKeyboard(singletonList(asList(ruButton, engButton)));
        return keyboard;
    }

    @Override
    public boolean isNeeded(Update update) {
        return nonNull(update.getMessage()) && START.equals(update.getMessage().getText());
    }
}
