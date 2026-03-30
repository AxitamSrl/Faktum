package com.faktum.model;

import com.faktum.model.enums.Locale;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "fiche_versions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ficheId", "locale", "version"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FicheVersion {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficheId", nullable = false)
    private Fiche fiche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Locale locale;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private String context;

    @Column(nullable = false)
    private String verdict;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String proArgs;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String contraArgs;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String data;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String sources;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
