package com.bot.tg.feeddy.bot;

import com.bot.tg.feeddy.domain.TelegramUpdate;
import com.bot.tg.feeddy.service.MessageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

@Getter
@Component
@RequiredArgsConstructor
public class FeeddyBot extends TelegramLongPollingBot {
    private final MessageService service;
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        TelegramUpdate telegramUpdate = null;

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
                    .callbackText(update.getCallbackQuery().getData())
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
}
