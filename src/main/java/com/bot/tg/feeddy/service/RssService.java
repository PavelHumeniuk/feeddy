package com.bot.tg.feeddy.service;

import com.bot.tg.feeddy.domain.News;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URL;

@Log4j2
@Service
public class RssService {

    @SneakyThrows
    public News parse(String source) {
        URL feedSource = new URL(source);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        SyndEntry lastPost = feed.getEntries().get(0);
        return new News(lastPost.getLink(),lastPost.getTitle());
    }
}
