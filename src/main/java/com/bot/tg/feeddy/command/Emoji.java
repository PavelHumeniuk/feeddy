package com.bot.tg.feeddy.command;

import lombok.Getter;

import static com.vdurmont.emoji.EmojiParser.parseToUnicode;

@Getter
public enum Emoji {
    RU_FLAG(":ru:", "ru"),
    ENG_FLAG(":us:", "eng"),
    PLUS("➕", "minus"),
    MINUS("➖", "plus"),
    CONFIG(":gear:", "config");

    private final String value;
    private final String name;

    Emoji(String value, String name) {
        this.value = parseToUnicode(value);
        this.name = name;
    }
}
