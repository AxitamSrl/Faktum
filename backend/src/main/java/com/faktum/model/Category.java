package com.faktum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Category {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String nameFr;

    @Column(nullable = false)
    private String nameNl;

    @Column(nullable = false)
    private String nameDe;

    @Column(nullable = false)
    private String nameEn;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Fiche> fiches = new ArrayList<>();
}
