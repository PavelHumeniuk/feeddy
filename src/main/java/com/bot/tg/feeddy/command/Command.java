package com.bot.tg.feeddy.command;

import com.bot.tg.feeddy.domain.TelegramUpdate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public interface Command {
    SendMessage execute(TelegramUpdate update);
    boolean isNeeded(TelegramUpdate update);
}
