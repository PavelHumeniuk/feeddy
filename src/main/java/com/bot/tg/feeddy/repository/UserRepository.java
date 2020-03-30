package com.bot.tg.feeddy.repository;

import com.bot.tg.feeddy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByChatId(Long chatId);
}
