package com.bot.tg.feeddy.command;


import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.entity.Source;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.bot.tg.feeddy.domain.Emoji.MINUS;
import static com.bot.tg.feeddy.domain.Phrase.*;

@RequiredArgsConstructor
@Component
public class DeleteRssCommand implements Command{
    private final UserRepository userRepository;

    @Transactional
    @Override
    @SneakyThrows
    public SendMessage execute(TelegramUpdate update) {
        SendMessage message = new SendMessage();

        Long chatId = update.getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);

        if (user.isPresent()){
            Set<Source> subscriptions = user.get().getSubscriptions();

            if (subscriptions.size()==0) message = new SendMessage()
                    .setChatId(update.getChatId())
                    .setText(getByLocale(NO_SUBSCRIPTIONS, user.get().getLocale()));

            message = createKeyboard(subscriptions)
                    .setChatId(update.getChatId())
                    .setText(getByLocale(DEl_MSG, user.get().getLocale()));
        }
        return message;
    }

    private SendMessage createKeyboard(Set<Source> subscriptions) throws MalformedURLException {
        SendMessage message = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (Source source : subscriptions){
            rowList.add(Collections.singletonList(new InlineKeyboardButton()
                    .setCallbackData(String.valueOf(source.getId()))
                    .setText(new URL(source.getLink()).getHost())));
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    @Override
    public boolean isNeeded(TelegramUpdate update) {
        return MINUS.getValue().equalsIgnoreCase(update.getText());
    }
}
