package site.easy.to.build.crm.service.tauxalerte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.TauxAlerte;
import site.easy.to.build.crm.repository.TauxAlerteRepository;

import java.util.Optional;

@Service
public class TauxAlerteService {

    @Autowired
    private TauxAlerteRepository tauxAlerteRepository;

    public TauxAlerte getLastTauxAlerte() {
        Optional<TauxAlerte> lastTauxAlerte = tauxAlerteRepository.findLastTauxAlerte();
        return lastTauxAlerte.orElse(null); // Retourne null si aucun TauxAlerte n'est trouvé
    }
}
