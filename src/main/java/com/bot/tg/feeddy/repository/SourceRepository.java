package com.bot.tg.feeddy.repository;


import com.bot.tg.feeddy.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceRepository extends JpaRepository<Source, Long> {

}
