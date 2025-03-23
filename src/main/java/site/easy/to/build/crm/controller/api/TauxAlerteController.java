package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.TauxAlerte;
import site.easy.to.build.crm.repository.TauxAlerteRepository;

@RestController
@RequestMapping("/api/taux-alerte")
public class TauxAlerteController {

    @Autowired
    private TauxAlerteRepository tauxAlerteRepository;

    @PostMapping("/create")
    public ResponseEntity<TauxAlerte> createTauxAlerte(@RequestBody TauxAlerte tauxAlerte) {
        System.out.println("TauxAlerte saved : "+ tauxAlerte.getTaux());
        TauxAlerte savedTauxAlerte = tauxAlerteRepository.save(tauxAlerte);
        return ResponseEntity.ok(savedTauxAlerte);
    }
}
