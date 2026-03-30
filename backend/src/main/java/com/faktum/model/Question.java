package com.faktum.model;

import com.faktum.model.enums.Locale;
import com.faktum.model.enums.QuestionStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Question {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficheId")
    private Fiche fiche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Locale locale = Locale.FR;

    private String authorName;

    private String authorEmail;

    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private QuestionStatus status = QuestionStatus.PENDING;

    private String rejectReason;

    private String answer;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String answerSources;

    private LocalDateTime approvedAt;

    private LocalDateTime answeredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
