package com.bot.tg.feeddy.command;


import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.domain.Phrase;
import com.bot.tg.feeddy.entity.Source;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

import static com.bot.tg.feeddy.domain.Emoji.DELETE;
import static com.bot.tg.feeddy.domain.Emoji.MINUS;
import static com.bot.tg.feeddy.domain.Emoji.RU_FLAG;
import static com.bot.tg.feeddy.domain.Phrase.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@RequiredArgsConstructor
@Component
public class DeleteSourceCommand implements Command{
    private final UserRepository userRepository;

    @Transactional
    @Override
    public List<SendMessage> execute(TelegramUpdate update) {
        ArrayList<SendMessage> messages = new ArrayList<>();
        Long chatId = update.getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);

        StringBuilder result = new StringBuilder();
        if (user.isPresent()){
            Set<Source> subscriptions = user.get().getSubscriptions();

            if (subscriptions.size()==0) messages.add(new SendMessage()
                    .setChatId(update.getChatId())
                    .setText(getByLocale(NO_SUBSCRIPTIONS, user.get().getLocale())));
            for (Source source : subscriptions) messages.add(new SendMessage()
                    .setChatId(update.getChatId())
                    .setText(source.getLink()).setReplyMarkup(createKeyboard(source.getLink())));
        }
        return messages;
    }
    private InlineKeyboardMarkup createKeyboard(String callbackData) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton delButton = new InlineKeyboardButton()
                .setText(DELETE.getValue())
                .setCallbackData(callbackData);

        keyboard.setKeyboard(singletonList(asList(delButton)));
        return keyboard;
    }

    @Override
    public boolean isNeeded(TelegramUpdate update) {
        return MINUS.getValue().equalsIgnoreCase(update.getText());
    }
}
