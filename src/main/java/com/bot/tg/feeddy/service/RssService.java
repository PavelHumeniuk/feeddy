package com.bot.tg.feeddy.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class RssService {

    @SneakyThrows
    public String parse(String source) {
        URL feedSource = new URL(source);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        return feed.getLink();
    }
}
