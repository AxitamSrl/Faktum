package com.faktum.service;

import com.faktum.dto.*;
import com.faktum.exception.ResourceNotFoundException;
import com.faktum.model.Question;
import com.faktum.model.enums.Locale;
import com.faktum.model.enums.QuestionStatus;
import com.faktum.repository.FicheRepository;
import com.faktum.repository.QuestionRepository;
import com.faktum.util.CuidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final FicheRepository ficheRepository;

    @Transactional(readOnly = true)
    public Page<QuestionDto> listQuestions(QuestionStatus status, Locale locale, Pageable pageable) {
        Page<Question> questions;
        if (status != null && locale != null) {
            questions = questionRepository.findByStatusAndLocale(status, locale, pageable);
        } else if (status != null) {
            questions = questionRepository.findByStatus(status, pageable);
        } else if (locale != null) {
            questions = questionRepository.findByLocale(locale, pageable);
        } else {
            questions = questionRepository.findAll(pageable);
        }
        return questions.map(this::toDto);
    }

    @Transactional
    public QuestionDto submitQuestion(SubmitQuestionRequest request) {
        var question = Question.builder()
                .id(CuidGenerator.generate())
                .text(request.getText())
                .locale(request.getLocale() != null ? request.getLocale() : Locale.FR)
                .authorName(request.getAuthorName())
                .authorEmail(request.getAuthorEmail())
                .build();

        if (request.getFicheSlug() != null) {
            var fiche = ficheRepository.findBySlug(request.getFicheSlug())
                    .orElseThrow(() -> new ResourceNotFoundException("Fiche", "slug", request.getFicheSlug()));
            question.setFiche(fiche);
        }

        question = questionRepository.save(question);
        return toDto(question);
    }

    @Transactional
    public QuestionDto approveQuestion(String id) {
        var question = findQuestion(id);
        if (question.getStatus() != QuestionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING questions can be approved");
        }
        question.setStatus(QuestionStatus.APPROVED);
        question.setApprovedAt(LocalDateTime.now());
        return toDto(questionRepository.save(question));
    }

    @Transactional
    public QuestionDto rejectQuestion(String id, String reason) {
        var question = findQuestion(id);
        if (question.getStatus() != QuestionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING questions can be rejected");
        }
        question.setStatus(QuestionStatus.REJECTED);
        question.setRejectReason(reason);
        return toDto(questionRepository.save(question));
    }

    @Transactional
    public QuestionDto answerQuestion(String id, String answer, String answerSources) {
        var question = findQuestion(id);
        if (question.getStatus() != QuestionStatus.PENDING && question.getStatus() != QuestionStatus.APPROVED) {
            throw new IllegalStateException("Only PENDING or APPROVED questions can be answered");
        }
        question.setStatus(QuestionStatus.ANSWERED);
        question.setAnswer(answer);
        question.setAnswerSources(answerSources);
        question.setAnsweredAt(LocalDateTime.now());
        return toDto(questionRepository.save(question));
    }

    private Question findQuestion(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
    }

    private QuestionDto toDto(Question q) {
        return QuestionDto.builder()
                .id(q.getId())
                .ficheSlug(q.getFiche() != null ? q.getFiche().getSlug() : null)
                .locale(q.getLocale())
                .authorName(q.getAuthorName())
                .text(q.getText())
                .status(q.getStatus())
                .rejectReason(q.getRejectReason())
                .answer(q.getAnswer())
                .answerSources(q.getAnswerSources())
                .approvedAt(q.getApprovedAt())
                .answeredAt(q.getAnsweredAt())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
