package com.faktum.controller;

import com.faktum.dto.*;
import com.faktum.model.enums.Locale;
import com.faktum.model.enums.QuestionStatus;
import com.faktum.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Page<QuestionDto> listQuestions(
            @RequestParam(required = false) QuestionStatus status,
            @RequestParam(required = false) Locale locale,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return questionService.listQuestions(status, locale, pageable);
    }

    @PostMapping
    public ResponseEntity<QuestionDto> submitQuestion(@Valid @RequestBody SubmitQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.submitQuestion(request));
    }

    @PutMapping("/{id}/approve")
    public QuestionDto approveQuestion(@PathVariable String id) {
        return questionService.approveQuestion(id);
    }

    @PutMapping("/{id}/reject")
    public QuestionDto rejectQuestion(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return questionService.rejectQuestion(id, body.get("reason"));
    }

    @PutMapping("/{id}/answer")
    public QuestionDto answerQuestion(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return questionService.answerQuestion(id, body.get("answer"), body.get("answerSources"));
    }
}
