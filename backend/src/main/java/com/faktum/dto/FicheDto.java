package com.faktum.dto;

import com.faktum.model.enums.FicheStatus;
import com.faktum.model.enums.FicheType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FicheDto {
    private String id;
    private String slug;
    private String categorySlug;
    private String categoryName;
    private FicheType ficheType;
    private FicheStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private FicheVersionDto latestVersion;
    private List<FicheVersionDto> versions;
}
