package com.faktum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevalidationService {

    private final Optional<CacheService> cacheService;

    public void revalidateFiche(String slug) {
        cacheService.ifPresent(CacheService::evictFicheCache);
        log.info("Revalidated fiche: {}", slug);
    }

    public void revalidateAll() {
        cacheService.ifPresent(CacheService::evictAllCaches);
        log.info("Revalidated all caches");
    }
}
