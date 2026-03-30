package com.faktum.dto;

import com.faktum.model.enums.Locale;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuestionRequest {

    @NotBlank
    @Size(min = 10)
    private String text;

    private Locale locale;
    private String ficheSlug;
    private String authorName;
    private String authorEmail;
}
