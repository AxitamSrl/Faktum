package com.faktum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CrawlLog {

    @Id
    private String id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String status;

    private String dataHash;

    private String ficheSlug;

    private String error;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
