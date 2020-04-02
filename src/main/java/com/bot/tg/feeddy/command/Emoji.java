package com.bot.tg.feeddy.command;

import lombok.Getter;

import static com.vdurmont.emoji.EmojiParser.parseToUnicode;

@Getter
public enum Emoji {
    RU_FLAG(":ru:", "RU"),
    ENG_FLAG(":us:", "ENG"),
    PLUS("➕", "minus"),
    MINUS("➖", "plus"),
    CONFIG(":gear:", "config"),
    LIKE(":thumbsup:","like"),
    DISLIKE(":thumbsdown:","dislike");

    private final String value;
    private final String name;


    Emoji(String value, String name) {
        this.value = parseToUnicode(value);
        this.name = name;
    }
}
