package com.bot.tg.feeddy.service;

import com.bot.tg.feeddy.domain.News;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RssService {

    @SneakyThrows
    public News getLastNews(String source) {
        SyndFeed feed = getSyndFeed(source);
        SyndEntry lastPost = feed.getEntries().get(0);
        return new News(lastPost.getLink(), lastPost.getTitle());
    }

    @SneakyThrows
    public List<News> getAllNews(String source) {
        SyndFeed feed = getSyndFeed(source);
        return feed.getEntries().stream()
                .map(entry -> new News(entry.getLink(), entry.getTitle()))
                .collect(Collectors.toList());
    }

    private SyndFeed getSyndFeed(String source) {
        SyndFeed build = new SyndFeedImpl();
        try {
            URL feedSource = new URL(source);
            SyndFeedInput input = new SyndFeedInput();
            build = input.build(new XmlReader(feedSource));
        } catch (Exception e) {
            log.error(e);
        }
        return build;

    }
}
