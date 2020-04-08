package com.bot.tg.feeddy.bot;

import com.bot.tg.feeddy.domain.News;
import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.entity.Source;
import com.bot.tg.feeddy.repository.SourceRepository;
import com.bot.tg.feeddy.repository.UserRepository;
import com.bot.tg.feeddy.service.MessageService;
import com.bot.tg.feeddy.service.RssService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class FeeddyBot extends TelegramLongPollingBot {
    private final MessageService service;
    private final RssService rssService;
    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        TelegramUpdate telegramUpdate = null;

        if (update.getMessage() == null && update.getCallbackQuery() == null) {
            return;
        }

        if (update.hasMessage()) {
             telegramUpdate= TelegramUpdate.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(update.getMessage().getText())
                    .userName(update.getMessage().getChat().getUserName())
                    .build();
        }

        if (update.hasCallbackQuery()) {
            answerCallBackAsync(update.getCallbackQuery().getId());
            telegramUpdate= TelegramUpdate.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId())
                    .text(update.getCallbackQuery().getData())
                    .userName(update.getCallbackQuery().getMessage().getChat().getUserName())
                    .callbackQuery(true)
                    .build();
        }

        SendMessage msg = service.resolve(telegramUpdate);
        execute(msg);
    }

    /**
     * send to user that button is processed
     * use to answer for each callback query
     */
    private void answerCallBackAsync(String callbackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setShowAlert(false);
        answerCallbackQuery.setCallbackQueryId(callbackQueryId);
        sendApiMethodAsync(answerCallbackQuery, new SentCallback<Boolean>() {
            @Override
            public void onResult(BotApiMethod<Boolean> method, Boolean response) {
            }

            @Override
            public void onError(BotApiMethod<Boolean> method, TelegramApiRequestException apiException) {
            }

            @Override
            public void onException(BotApiMethod<Boolean> method, Exception exception) {
            }
        });
    }


    //    @Scheduled(cron = "${bot.cronDelay}")
    @Transactional
    @Scheduled(fixedDelay = 10000)
    public void sendUpdate() {
        Map<Source, List<News>> newsBySource = sourceRepository.findAll()
                .parallelStream()
                .collect(Collectors.toMap(Function.identity(), this::createNews, (news, news2) -> news2));

        userRepository.findAll()
                .parallelStream()
                .flatMap(user -> user.getSubscriptions().stream()
                        .flatMap(key -> newsBySource.get(key).stream())
                        .map(news -> new SendMessage(user.getChatId(), news.getLinkWithTitle()).enableMarkdownV2(true)))
                .forEach(this::sendMessage);
    }

    private List<News> createNews(Source source) {
        List<News> allNews = rssService.getAllNews(source.getLink());
        List<News> result = new LinkedList<>();
        for (News item : allNews) {
            if (item.getLink().equals(source.getLastPost())) {
                break;
            }
            result.add(item);
        }
        return result;
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
