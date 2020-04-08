package com.bot.tg.feeddy.service;

import com.bot.tg.feeddy.domain.News;
import com.bot.tg.feeddy.entity.Source;
import com.bot.tg.feeddy.repository.SourceRepository;
import com.bot.tg.feeddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;
    private final RssService rssService;

    public List<SendMessage> getAllMessagesForRss(){
        Map<Source, List<News>> newsBySource = sourceRepository.findAll()
                .parallelStream()
                .collect(Collectors.toMap(Function.identity(), this::createNews, (news, news2) -> news2));

        return userRepository.findAll()
                .parallelStream()
                .flatMap(user -> user.getSubscriptions().stream()
                        .flatMap(key -> newsBySource.get(key).stream())
                        .map(news -> new SendMessage(user.getChatId(), news.getLinkWithTitle()).enableMarkdownV2(true)))
                .collect(Collectors.toList());
    }

    private List<News> createNews(Source source) {

        List<News> allNews = rssService.getAllNews(source.getLink());
        List<News> result = new LinkedList<>();
        for (News item : allNews) {
            if (item.getLink().equals(source.getLastPost())) {
                break;
            }
            result.add(item);
        }
        return result;
    }
}
