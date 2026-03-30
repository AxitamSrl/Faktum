package com.faktum.controller;

import com.faktum.dto.StatisticsDto;
import com.faktum.model.enums.FicheStatus;
import com.faktum.model.enums.Locale;
import com.faktum.model.enums.QuestionStatus;
import com.faktum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final FicheRepository ficheRepository;
    private final FicheVersionRepository versionRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public StatisticsDto getStatistics() {
        var versionsByLocale = new HashMap<String, Long>();
        for (var locale : Locale.values()) {
            versionsByLocale.put(locale.name(), versionRepository.countByLocale(locale));
        }

        return StatisticsDto.builder()
                .totalFiches(ficheRepository.count())
                .publishedFiches(ficheRepository.countByStatus(FicheStatus.PUBLISHED))
                .draftFiches(ficheRepository.countByStatus(FicheStatus.DRAFT))
                .archivedFiches(ficheRepository.countByStatus(FicheStatus.ARCHIVED))
                .versionsByLocale(versionsByLocale)
                .totalQuestions(questionRepository.count())
                .pendingQuestions(questionRepository.countByStatus(QuestionStatus.PENDING))
                .answeredQuestions(questionRepository.countByStatus(QuestionStatus.ANSWERED))
                .totalCategories(categoryRepository.count())
                .build();
    }
}
