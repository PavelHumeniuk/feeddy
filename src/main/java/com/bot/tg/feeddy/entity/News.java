package com.bot.tg.feeddy.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class News {

    private String link;
    private String name;

    public String getLinkWithTitle(){
        return String.format("[%s](%s)", this.name, this.link);
    }
}
