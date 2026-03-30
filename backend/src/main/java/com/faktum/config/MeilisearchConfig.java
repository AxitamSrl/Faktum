package com.faktum.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "meilisearch.enabled", havingValue = "true")
public class MeilisearchConfig {

    @Value("${meilisearch.url:http://localhost:7700}")
    private String url;

    @Value("${meilisearch.api-key:REDACTED}")
    private String apiKey;

    @Bean
    public Client meilisearchClient() {
        return new Client(new Config(url, apiKey));
    }
}
