package com.faktum.repository;

import com.faktum.model.FicheVersion;
import com.faktum.model.enums.Locale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FicheVersionRepository extends JpaRepository<FicheVersion, String> {

    List<FicheVersion> findByFicheIdAndLocaleOrderByVersionDesc(String ficheId, Locale locale);

    @Query("SELECT fv FROM FicheVersion fv WHERE fv.fiche.id = :ficheId AND fv.locale = :locale ORDER BY fv.version DESC LIMIT 1")
    Optional<FicheVersion> findLatestVersion(@Param("ficheId") String ficheId, @Param("locale") Locale locale);

    @Query("SELECT MAX(fv.version) FROM FicheVersion fv WHERE fv.fiche.id = :ficheId AND fv.locale = :locale")
    Optional<Integer> findMaxVersion(@Param("ficheId") String ficheId, @Param("locale") Locale locale);

    @Query("SELECT DISTINCT fv.locale FROM FicheVersion fv WHERE fv.fiche.id = :ficheId")
    List<Locale> findLocalesByFicheId(@Param("ficheId") String ficheId);

    long countByLocale(Locale locale);
}
