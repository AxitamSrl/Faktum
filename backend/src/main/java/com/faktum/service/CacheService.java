package com.faktum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final CacheManager cacheManager;

    public void evictFicheCache() {
        var cache = cacheManager.getCache("fiches");
        if (cache != null) {
            cache.clear();
            log.info("Fiches cache cleared");
        }
    }

    public void evictCategoryCache() {
        var cache = cacheManager.getCache("categories");
        if (cache != null) {
            cache.clear();
            log.info("Categories cache cleared");
        }
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
        log.info("All caches cleared");
    }
}
