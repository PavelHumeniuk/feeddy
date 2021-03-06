package com.bot.tg.feeddy.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Phrase {
    LANGUAGE_SAVED("Language saved!", "Язык сохранен!"),
    ASK_ADD_SOURCE("Please, give me RSS link!", "Пожалуйста, дай RSS ссылку!"),
    NO_SUBSCRIPTIONS("You haven't RSS-links yet", "У вас нет RSS подписок"),
    DEl_MSG("Remove the RSS Feed:", "Выбери RSS чтобы удалить:"),
    DEL_MSG_TRUE("RSS was deleted","RSS был удален")
    ;

    private final String eng;
    private final String ru;

    public static String getByLocale(Phrase phrase, Locale locale) {
        return locale == Locale.RU ? phrase.ru : phrase.eng;
    }
}
