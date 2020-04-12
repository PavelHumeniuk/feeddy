package com.bot.tg.feeddy.domain;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramUpdate {
    private Long chatId;
    private String text;
    private String userName;
    private String data;
    private boolean callbackQuery;
}
