package com.bot.tg.feeddy.command;


import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.entity.Phrase;
import com.bot.tg.feeddy.entity.Source;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.Optional;
import java.util.Set;

import static com.bot.tg.feeddy.domain.Emoji.MINUS;
import static com.bot.tg.feeddy.entity.Phrase.ASK_ADD_SOURCE;

@RequiredArgsConstructor
@Component
public class DeleteSourceCommand implements Command{
    private final UserRepository userRepository;

    @Transactional
    @Override
    public SendMessage execute(TelegramUpdate update) {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);

        StringBuilder result = new StringBuilder();
        if (user.isPresent()){
            Set<Source> subscriptions = user.get().getSubscriptions();

            if (subscriptions.size()==0) result = new StringBuilder(Phrase.getByLocale(ASK_ADD_SOURCE, user.get().getLocale()));
            for (Source source : subscriptions) result.append(source.getLink()).append("\n");
        }

        return sendMessage.setChatId(update.getChatId())
                .setText(String.valueOf(result));
    }

    @Override
    public boolean isNeeded(TelegramUpdate update) {
        return MINUS.getValue().equalsIgnoreCase(update.getText());
    }
}
