package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.domain.Locale;
import com.bot.tg.feeddy.domain.Phrase;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.bot.tg.feeddy.domain.Emoji.CONFIG;
import static com.bot.tg.feeddy.domain.Emoji.ENG_FLAG;
import static com.bot.tg.feeddy.domain.Emoji.MINUS;
import static com.bot.tg.feeddy.domain.Emoji.PLUS;
import static com.bot.tg.feeddy.domain.Emoji.RU_FLAG;
import static com.bot.tg.feeddy.domain.Phrase.LANGUAGE_SAVED;
import static java.util.Arrays.asList;

@Log4j2
@Component
@RequiredArgsConstructor
public class SaveLocaleCommand implements Command {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public SendMessage execute(TelegramUpdate update) {
        Long chatId = update.getChatId();
        Locale locale = Locale.valueOf(update.getText());
        log.info("Save locale {} for chat id {}", locale, chatId);

        Optional<User> user = userRepository.findByChatId(chatId);
        user.ifPresent(data -> data.setLocale(locale));

        return new SendMessage(chatId, Phrase.getByLocale(LANGUAGE_SAVED, locale))
                .setReplyMarkup(createKeyboard());
    }

    private ReplyKeyboardMarkup createKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        row.add(PLUS.getValue());
        row.add(MINUS.getValue());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(CONFIG.getValue());

        keyboard.setKeyboard(asList(row, secondRow));
        return keyboard;
    }

    @Override
    public boolean isNeeded(TelegramUpdate update) {
        return update.isCallbackQuery()
                && (ENG_FLAG.getName().equals(update.getData()))
                || RU_FLAG.getName().equals(update.getData());
    }
}
