package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.entity.Source;
import com.bot.tg.feeddy.entity.User;
import com.bot.tg.feeddy.domain.News;
import com.bot.tg.feeddy.repository.SourceRepository;
import com.bot.tg.feeddy.repository.UserRepository;
import com.bot.tg.feeddy.service.RssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Optional;

import static com.bot.tg.feeddy.domain.Emoji.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
public class AddSourceCommand implements Command {
    public static final String LINK_PATTERN = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    private final RssService rssService;
    private final UserRepository userRepository;
    private final SourceRepository sourceRepository;

    @Transactional
    @Override
    public SendMessage execute(TelegramUpdate update) {
        Long chatId = update.getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);
        News lastNews = rssService.getLastNews(update.getText());
        Source source = new Source();
        source.setLastPost(lastNews.getLink().trim());
        source.setLink(update.getText());
        Source savedSource = sourceRepository.findByLink(source.getLink())
                .orElseGet(() -> sourceRepository.save(source));
        user.ifPresent(data -> data.getSubscriptions().add(savedSource));
        return new SendMessage()
                .setChatId(chatId)
                .setText(lastNews.getLinkWithTitle())
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
    public boolean isNeeded(TelegramUpdate update) {
        return update.getText().matches(LINK_PATTERN);
    }
}
