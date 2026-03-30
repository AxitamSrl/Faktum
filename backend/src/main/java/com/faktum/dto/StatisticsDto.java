package com.faktum.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private long totalFiches;
    private long publishedFiches;
    private long draftFiches;
    private long archivedFiches;
    private Map<String, Long> versionsByLocale;
    private long totalQuestions;
    private long pendingQuestions;
    private long answeredQuestions;
    private long totalCategories;
}
