package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.Locale;
import com.bot.tg.feeddy.domain.Phrase;
import com.bot.tg.feeddy.domain.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.bot.tg.feeddy.command.Emoji.PLUS;
import static com.bot.tg.feeddy.domain.Phrase.ASK_ADD_SOURCE;

@RequiredArgsConstructor
@Component
public class AskSourceCommand implements Command {
    private final UserRepository userRepository;

    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Locale locale = userRepository.findByChatId(chatId)
                .map(User::getLocale)
                .orElse(Locale.ENG);
        return new SendMessage(chatId, Phrase.getByLocale(ASK_ADD_SOURCE, locale));
    }

    @Override
    public boolean isNeeded(Update update) {
        return update.getMessage() != null && PLUS.getValue().equalsIgnoreCase(update.getMessage().getText());
    }
}
