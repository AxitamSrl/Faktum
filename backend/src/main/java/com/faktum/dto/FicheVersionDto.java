package com.faktum.dto;

import com.faktum.model.enums.Locale;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FicheVersionDto {
    private String id;
    private Locale locale;
    private Integer version;
    private String title;
    private String subtitle;
    private String summary;
    private String context;
    private String verdict;
    private String proArgs;
    private String contraArgs;
    private String data;
    private String sources;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
