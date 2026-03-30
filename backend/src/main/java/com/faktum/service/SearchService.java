package com.faktum.service;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
@ConditionalOnProperty(name = "meilisearch.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final Client meilisearchClient;
    private static final String INDEX_NAME = "fiches";

    @PostConstruct
    public void initIndex() {
        try {
            meilisearchClient.createIndex(INDEX_NAME, "id");
            var index = meilisearchClient.index(INDEX_NAME);
            index.updateFilterableAttributesSettings(new String[]{"locale", "categorySlug", "status", "ficheType"});
            index.updateSearchableAttributesSettings(new String[]{"title", "summary", "context", "verdict"});
        } catch (Exception e) {
            log.warn("Could not initialize Meilisearch index: {}", e.getMessage());
        }
    }

    public void indexFiche(Map<String, Object> document) {
        try {
            var index = meilisearchClient.index(INDEX_NAME);
            index.addDocuments("[" + new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(document) + "]");
        } catch (Exception e) {
            log.error("Failed to index fiche: {}", e.getMessage());
        }
    }

    public void removeFiche(String id) {
        try {
            var index = meilisearchClient.index(INDEX_NAME);
            index.deleteDocument(id);
        } catch (Exception e) {
            log.error("Failed to remove fiche from index: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> search(String query, String locale, String categorySlug, int limit) {
        try {
            var index = meilisearchClient.index(INDEX_NAME);
            var filters = new ArrayList<String>();
            filters.add("status = PUBLISHED");
            if (locale != null) filters.add("locale = " + locale);
            if (categorySlug != null) filters.add("categorySlug = " + categorySlug);

            var searchRequest = SearchRequest.builder()
                    .q(query)
                    .filter(new String[]{String.join(" AND ", filters)})
                    .limit(limit)
                    .build();

            SearchResult result = (SearchResult) index.search(searchRequest);
            return result.getHits() != null
                    ? (List<Map<String, Object>>) (List<?>) result.getHits()
                    : List.of();
        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return List.of();
        }
    }
}
