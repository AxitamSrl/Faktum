package com.faktum.repository;

import com.faktum.model.CrawlLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrawlLogRepository extends JpaRepository<CrawlLog, String> {

    List<CrawlLog> findBySource(String source);

    List<CrawlLog> findByFicheSlug(String ficheSlug);
}
