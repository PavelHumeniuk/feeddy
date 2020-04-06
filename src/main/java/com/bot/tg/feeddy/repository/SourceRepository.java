package com.bot.tg.feeddy.repository;


import com.bot.tg.feeddy.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Long> {

    Optional<Source> findByLink(String link);
}
