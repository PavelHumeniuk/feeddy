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
    private String callbackText;

    public boolean hasCallback(){
        return this.callbackText != null;
    }
}
