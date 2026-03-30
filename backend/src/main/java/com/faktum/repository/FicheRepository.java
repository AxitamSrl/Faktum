package com.faktum.repository;

import com.faktum.model.Fiche;
import com.faktum.model.enums.FicheStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FicheRepository extends JpaRepository<Fiche, String> {

    Optional<Fiche> findBySlug(String slug);

    Page<Fiche> findByStatus(FicheStatus status, Pageable pageable);

    @Query("SELECT f FROM Fiche f WHERE f.status = :status AND f.category.slug = :categorySlug")
    Page<Fiche> findByStatusAndCategorySlug(@Param("status") FicheStatus status,
                                             @Param("categorySlug") String categorySlug,
                                             Pageable pageable);

    long countByStatus(FicheStatus status);
}
