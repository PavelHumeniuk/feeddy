package com.bot.tg.feeddy.service;

import com.bot.tg.feeddy.command.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final List<Command> commands;

    public SendMessage resolve(Update update) {
        Long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        return commands.stream()
                .filter(command -> command.isNeeded(update))
                .findFirst()
                .map(command -> command.execute(update))
                // TODO: 3/30/2020 fix default msg
                .orElse(new SendMessage(chatId, "Hi!"));
    }
}
