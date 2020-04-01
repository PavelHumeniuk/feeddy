package com.bot.tg.feeddy.repository;

import com.bot.tg.feeddy.domain.Locale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocaleRepository extends JpaRepository<Locale, Long> {
    Optional<Locale> findByName(String name);
}
