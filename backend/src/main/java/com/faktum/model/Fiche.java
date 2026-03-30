package com.faktum.model;

import com.faktum.model.enums.FicheStatus;
import com.faktum.model.enums.FicheType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fiches")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Fiche {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FicheType ficheType = FicheType.ANALYSE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FicheStatus status = FicheStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fiche", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FicheVersion> versions = new ArrayList<>();

    @OneToMany(mappedBy = "fiche")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
