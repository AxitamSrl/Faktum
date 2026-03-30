package com.faktum.repository;

import com.faktum.model.Question;
import com.faktum.model.enums.Locale;
import com.faktum.model.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {

    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);

    Page<Question> findByStatusAndLocale(QuestionStatus status, Locale locale, Pageable pageable);

    Page<Question> findByLocale(Locale locale, Pageable pageable);

    long countByStatus(QuestionStatus status);
}
