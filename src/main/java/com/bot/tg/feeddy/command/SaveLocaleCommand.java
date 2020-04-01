package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.Locale;
import com.bot.tg.feeddy.domain.User;
import com.bot.tg.feeddy.repository.LocaleRepository;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Optional;

import static com.bot.tg.feeddy.command.Emoji.CONFIG;
import static com.bot.tg.feeddy.command.Emoji.ENG_FLAG;
import static com.bot.tg.feeddy.command.Emoji.MINUS;
import static com.bot.tg.feeddy.command.Emoji.PLUS;
import static com.bot.tg.feeddy.command.Emoji.RU_FLAG;
import static java.util.Arrays.asList;

@Log4j2
@Component
@RequiredArgsConstructor
public class SaveLocaleCommand implements Command {
    public static final String LANGUAGE_SAVED = "Language saved!";
    private final UserRepository userRepository;
    private final LocaleRepository localeRepository;

    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String localeName = update.getCallbackQuery().getData();
        log.info("Save locale {} for chat id {}", localeName, chatId);

        Optional<Locale> locale = localeRepository.findByName(localeName);
        Optional<User> user = userRepository.findByChatId(chatId);

        user.ifPresent(data -> locale.ifPresent(data::setLocale));

        return new SendMessage(chatId, LANGUAGE_SAVED)
                .setReplyMarkup(createKeyboard());
    }

    private ReplyKeyboardMarkup createKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        row.add(MINUS.getValue());
        row.add(PLUS.getValue());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(CONFIG.getValue());

        keyboard.setKeyboard(asList(row, secondRow));
        return keyboard;
    }

    @Override
    public boolean isNeeded(Update update) {
        String data = update.getCallbackQuery().getData();
        return update.hasCallbackQuery()
                && (ENG_FLAG.getName().equals(data) || RU_FLAG.getName().equals(data));
    }
}
