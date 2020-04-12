package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.Locale;
import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.repository.SourceRepository;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

import static com.bot.tg.feeddy.domain.Phrase.DEL_MSG_TRUE;
import static com.bot.tg.feeddy.domain.Phrase.DEl_MSG;
import static com.bot.tg.feeddy.domain.Phrase.getByLocale;

@Component
@RequiredArgsConstructor
public class DeleteSourceCommand implements Command {
    private final UserRepository userRepository;
    private final SourceRepository sourceRepository;
    private Locale locale;

    @Transactional
    @Override
    public SendMessage execute(TelegramUpdate update) {
        Long chatId = update.getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);
        sourceRepository.findById(Long.valueOf(update.getData()))
                .ifPresent(data -> user.ifPresent(userdata -> userdata.getSubscriptions().remove(data)));

        return new SendMessage().setText(getByLocale(DEL_MSG_TRUE, locale)).setChatId(chatId);
    }

    @Override
    public boolean isNeeded(TelegramUpdate update) {
        Long chatId = update.getChatId();
        locale = userRepository.findByChatId(chatId)
                .map(User::getLocale)
                .orElse(Locale.ENG);

        return update.isCallbackQuery()
                && update.getText().equals(getByLocale(DEl_MSG, locale));
    }
}
