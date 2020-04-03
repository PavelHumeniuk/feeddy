package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.service.RssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.bot.tg.feeddy.command.Emoji.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
public class AddSourceCommand implements Command {
    public static final String LINK_PATTERN = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    private final RssService rssService;

    @Override
    public SendMessage execute(Update update) {

        Long chatId = update.getMessage().getChatId();
        String link = rssService.parse(update.getMessage().getText());
        return new SendMessage()
                .setChatId(chatId)
                .setText(link)
                .enableMarkdownV2(true)
                .setReplyMarkup(createKeyboard());
    }

    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton likeButton = new InlineKeyboardButton()
                .setText(LIKE.getValue())
                .setCallbackData(LIKE.getName());

        InlineKeyboardButton dislikeButton = new InlineKeyboardButton()
                .setText(DISLIKE.getValue())
                .setCallbackData(DISLIKE.getName());

        keyboard.setKeyboard(singletonList(asList(likeButton, dislikeButton)));
        return keyboard;
    }

    @Override
    public boolean isNeeded(Update update) {
        return update.getMessage() != null && update.getMessage().getText().matches(LINK_PATTERN);
    }
}
