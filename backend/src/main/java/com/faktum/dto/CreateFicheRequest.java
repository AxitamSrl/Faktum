package com.faktum.dto;

import com.faktum.model.enums.FicheType;
import com.faktum.model.enums.Locale;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFicheRequest {

    @NotBlank
    @Size(min = 3, max = 200)
    private String slug;

    @NotBlank
    private String categorySlug;

    @NotNull
    private FicheType ficheType;

    @NotNull
    private Locale locale;

    @NotBlank
    @Size(min = 3, max = 200)
    private String title;

    private String subtitle;

    @NotBlank
    @Size(min = 50)
    private String summary;

    @Size(min = 50)
    private String context;

    @Size(min = 30)
    private String verdict;

    private String proArgs;
    private String contraArgs;
    private String data;
    private String sources;
}
