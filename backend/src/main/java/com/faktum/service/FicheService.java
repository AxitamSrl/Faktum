package com.faktum.service;

import com.faktum.dto.*;
import com.faktum.exception.ResourceNotFoundException;
import com.faktum.model.*;
import com.faktum.model.enums.*;
import com.faktum.model.enums.Locale;
import com.faktum.repository.*;
import com.faktum.util.CuidGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FicheService {

    private final FicheRepository ficheRepository;
    private final FicheVersionRepository versionRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Page<FicheDto> listFiches(FicheStatus status, String categorySlug, Locale locale, Pageable pageable) {
        Page<Fiche> fiches;
        if (categorySlug != null && !categorySlug.isBlank()) {
            fiches = ficheRepository.findByStatusAndCategorySlug(
                    status != null ? status : FicheStatus.PUBLISHED, categorySlug, pageable);
        } else {
            fiches = ficheRepository.findByStatus(
                    status != null ? status : FicheStatus.PUBLISHED, pageable);
        }
        Locale effectiveLocale = locale != null ? locale : Locale.FR;
        return fiches.map(f -> toDto(f, effectiveLocale));
    }

    @Transactional(readOnly = true)
    public FicheDto getFicheBySlug(String slug, Locale locale) {
        var fiche = ficheRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche", "slug", slug));
        Locale effectiveLocale = locale != null ? locale : Locale.FR;
        var dto = toDto(fiche, effectiveLocale);
        dto.setVersions(fiche.getVersions().stream()
                .filter(v -> v.getLocale() == effectiveLocale)
                .sorted(Comparator.comparingInt(FicheVersion::getVersion).reversed())
                .map(this::toVersionDto)
                .toList());
        return dto;
    }

    @Transactional
    public FicheDto createFiche(CreateFicheRequest request) {
        var category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", request.getCategorySlug()));

        validateFicheContent(request.getFicheType(), request.getProArgs(), request.getContraArgs());

        var fiche = Fiche.builder()
                .id(CuidGenerator.generate())
                .slug(request.getSlug())
                .category(category)
                .ficheType(request.getFicheType())
                .build();
        fiche = ficheRepository.save(fiche);

        var version = FicheVersion.builder()
                .id(CuidGenerator.generate())
                .fiche(fiche)
                .locale(request.getLocale())
                .version(1)
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .summary(request.getSummary())
                .context(request.getContext())
                .verdict(request.getVerdict())
                .proArgs(request.getProArgs())
                .contraArgs(request.getContraArgs())
                .data(request.getData())
                .sources(request.getSources())
                .build();
        versionRepository.save(version);

        return toDto(fiche, request.getLocale());
    }

    @Transactional
    public FicheDto updateFiche(String slug, UpdateFicheRequest request) {
        var fiche = ficheRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche", "slug", slug));

        validateFicheContent(fiche.getFicheType(), request.getProArgs(), request.getContraArgs());

        int nextVersion = versionRepository.findMaxVersion(fiche.getId(), request.getLocale())
                .orElse(0) + 1;

        var version = FicheVersion.builder()
                .id(CuidGenerator.generate())
                .fiche(fiche)
                .locale(request.getLocale())
                .version(nextVersion)
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .summary(request.getSummary())
                .context(request.getContext())
                .verdict(request.getVerdict())
                .proArgs(request.getProArgs())
                .contraArgs(request.getContraArgs())
                .data(request.getData())
                .sources(request.getSources())
                .build();
        versionRepository.save(version);

        return toDto(fiche, request.getLocale());
    }

    @Transactional
    public FicheDto publishFiche(String slug) {
        var fiche = ficheRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche", "slug", slug));

        if (fiche.getStatus() != FicheStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT fiches can be published");
        }

        fiche.setStatus(FicheStatus.PUBLISHED);
        ficheRepository.save(fiche);

        var now = LocalDateTime.now();
        fiche.getVersions().stream()
                .filter(v -> v.getPublishedAt() == null)
                .forEach(v -> {
                    v.setPublishedAt(now);
                    versionRepository.save(v);
                });

        return toDto(fiche, Locale.FR);
    }

    @Transactional
    public FicheDto archiveFiche(String slug) {
        var fiche = ficheRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche", "slug", slug));

        fiche.setStatus(FicheStatus.ARCHIVED);
        ficheRepository.save(fiche);

        return toDto(fiche, Locale.FR);
    }

    @Transactional
    public FicheDto translateFiche(String slug, UpdateFicheRequest request) {
        var fiche = ficheRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Fiche", "slug", slug));

        int nextVersion = versionRepository.findMaxVersion(fiche.getId(), request.getLocale())
                .orElse(0) + 1;

        var versionBuilder = FicheVersion.builder()
                .id(CuidGenerator.generate())
                .fiche(fiche)
                .locale(request.getLocale())
                .version(nextVersion)
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .summary(request.getSummary())
                .context(request.getContext())
                .verdict(request.getVerdict())
                .proArgs(request.getProArgs())
                .contraArgs(request.getContraArgs())
                .data(request.getData())
                .sources(request.getSources());

        if (fiche.getStatus() == FicheStatus.PUBLISHED) {
            versionBuilder.publishedAt(LocalDateTime.now());
        }

        versionRepository.save(versionBuilder.build());
        return toDto(fiche, request.getLocale());
    }

    private void validateFicheContent(FicheType type, String proArgsJson, String contraArgsJson) {
        if (type == FicheType.ANALYSE) {
            try {
                List<Map<String, Object>> proArgs = proArgsJson != null
                        ? objectMapper.readValue(proArgsJson, new TypeReference<>() {})
                        : List.of();
                List<Map<String, Object>> contraArgs = contraArgsJson != null
                        ? objectMapper.readValue(contraArgsJson, new TypeReference<>() {})
                        : List.of();

                if (proArgs.size() != 5 || contraArgs.size() != 5) {
                    throw new IllegalArgumentException("ANALYSE fiches require exactly 5 PRO and 5 CONTRA arguments");
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON for arguments: " + e.getMessage());
            }
        }
    }

    private FicheDto toDto(Fiche fiche, Locale locale) {
        var latestVersion = versionRepository.findLatestVersion(fiche.getId(), locale)
                .map(this::toVersionDto)
                .orElse(null);

        return FicheDto.builder()
                .id(fiche.getId())
                .slug(fiche.getSlug())
                .categorySlug(fiche.getCategory() != null ? fiche.getCategory().getSlug() : null)
                .categoryName(fiche.getCategory() != null ? getCategoryName(fiche.getCategory(), locale) : null)
                .ficheType(fiche.getFicheType())
                .status(fiche.getStatus())
                .createdAt(fiche.getCreatedAt())
                .updatedAt(fiche.getUpdatedAt())
                .latestVersion(latestVersion)
                .build();
    }

    private FicheVersionDto toVersionDto(FicheVersion v) {
        return FicheVersionDto.builder()
                .id(v.getId())
                .locale(v.getLocale())
                .version(v.getVersion())
                .title(v.getTitle())
                .subtitle(v.getSubtitle())
                .summary(v.getSummary())
                .context(v.getContext())
                .verdict(v.getVerdict())
                .proArgs(v.getProArgs())
                .contraArgs(v.getContraArgs())
                .data(v.getData())
                .sources(v.getSources())
                .publishedAt(v.getPublishedAt())
                .createdAt(v.getCreatedAt())
                .build();
    }

    private String getCategoryName(Category category, Locale locale) {
        return switch (locale) {
            case NL -> category.getNameNl();
            case DE -> category.getNameDe();
            case EN -> category.getNameEn();
            default -> category.getNameFr();
        };
    }
}
