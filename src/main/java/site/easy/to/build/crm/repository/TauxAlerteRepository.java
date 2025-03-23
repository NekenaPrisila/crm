package site.easy.to.build.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.easy.to.build.crm.entity.TauxAlerte;

public interface TauxAlerteRepository extends JpaRepository<TauxAlerte, Long> {
    // Requête JPQL pour récupérer le dernier TauxAlerte
    @Query("SELECT t FROM TauxAlerte t ORDER BY t.dateChangement DESC LIMIT 1")
    Optional<TauxAlerte> findLastTauxAlerte();
}
