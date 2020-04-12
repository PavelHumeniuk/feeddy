package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.Locale;
import com.bot.tg.feeddy.domain.Phrase;
import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.bot.tg.feeddy.domain.Emoji.PLUS;
import static com.bot.tg.feeddy.domain.Phrase.ASK_ADD_SOURCE;

@RequiredArgsConstructor
@Component
public class AskSourceCommand implements Command {
    private final UserRepository userRepository;

    @Override
    public SendMessage execute(TelegramUpdate update) {
        Long chatId = update.getChatId();
        Locale locale = userRepository.findByChatId(chatId)
                .map(User::getLocale)
                .orElse(Locale.ENG);
        return new SendMessage(chatId, Phrase.getByLocale(ASK_ADD_SOURCE, locale));
    }

    @Override
    public boolean isNeeded(TelegramUpdate update) {
        return PLUS.getValue().equalsIgnoreCase(update.getData());
    }
}
