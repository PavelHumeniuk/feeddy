package com.bot.tg.feeddy.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Phrase {
    LANGUAGE_SAVED("Language saved!", "Язык сохранен!"),
    ASK_ADD_SOURCE("Please, give me RSS link!", "Пожалуйста, дай RSS ссылку!")
    ;

    private final String eng;
    private final String ru;

    public static String getByLocale(Phrase phrase, Locale locale) {
        return locale == Locale.RU ? phrase.ru : phrase.eng;
    }
}
