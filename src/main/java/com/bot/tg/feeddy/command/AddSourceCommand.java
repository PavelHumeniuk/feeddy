package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.Source;
import com.bot.tg.feeddy.domain.User;
import com.bot.tg.feeddy.repository.UserRepository;
import com.bot.tg.feeddy.service.RssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Optional;

import static com.bot.tg.feeddy.command.Emoji.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
public class AddSourceCommand implements Command {
    public static final String LINK_PATTERN = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    private final RssService rssService;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);
        String linkWithDescription = rssService.parsePostLinkWithDescr(update.getMessage().getText());
        String linkLastPost = rssService.parsePostLink(update.getMessage().getText());
        Source source = new Source();
        source.setLastPost(linkLastPost);
        source.setLink(update.getMessage().getText());
        user.ifPresent(data -> data.getSubscriptions().add(source));
        return new SendMessage()
                .setChatId(chatId)
                .setText(linkWithDescription)
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
