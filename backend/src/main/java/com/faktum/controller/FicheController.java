package com.faktum.controller;

import com.faktum.dto.*;
import com.faktum.model.enums.FicheStatus;
import com.faktum.model.enums.Locale;
import com.faktum.service.FicheService;
import com.faktum.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fiches")
@RequiredArgsConstructor
public class FicheController {

    private final FicheService ficheService;
    private final Optional<SearchService> searchService;

    @GetMapping
    public Page<FicheDto> listFiches(
            @RequestParam(required = false) FicheStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Locale locale,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ficheService.listFiches(status, category, locale, pageable);
    }

    @GetMapping("/{slug}")
    public FicheDto getFiche(
            @PathVariable String slug,
            @RequestParam(required = false) Locale locale) {
        return ficheService.getFicheBySlug(slug, locale);
    }

    @PostMapping
    public ResponseEntity<FicheDto> createFiche(@Valid @RequestBody CreateFicheRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ficheService.createFiche(request));
    }

    @PutMapping("/{slug}")
    public FicheDto updateFiche(
            @PathVariable String slug,
            @Valid @RequestBody UpdateFicheRequest request) {
        return ficheService.updateFiche(slug, request);
    }

    @PostMapping("/{slug}/publish")
    public FicheDto publishFiche(@PathVariable String slug) {
        return ficheService.publishFiche(slug);
    }

    @PostMapping("/{slug}/archive")
    public FicheDto archiveFiche(@PathVariable String slug) {
        return ficheService.archiveFiche(slug);
    }

    @PostMapping("/{slug}/translate")
    public FicheDto translateFiche(
            @PathVariable String slug,
            @Valid @RequestBody UpdateFicheRequest request) {
        return ficheService.translateFiche(slug, request);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchFiches(
            @RequestParam String q,
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") int limit) {
        return searchService.map(s -> s.search(q, locale, category, limit)).orElse(List.of());
    }
}
