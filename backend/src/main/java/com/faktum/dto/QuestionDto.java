package com.faktum.dto;

import com.faktum.model.enums.Locale;
import com.faktum.model.enums.QuestionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private String id;
    private String ficheSlug;
    private Locale locale;
    private String authorName;
    private String text;
    private QuestionStatus status;
    private String rejectReason;
    private String answer;
    private String answerSources;
    private LocalDateTime approvedAt;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
}
