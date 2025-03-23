package site.easy.to.build.crm.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "taux_alerte")
public class TauxAlerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taux", nullable = false)
    private Double taux;

    @Column(name = "date_changement", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateChangement;

    // Constructeurs
    public TauxAlerte() {
    }

    public TauxAlerte(Double taux, LocalDateTime dateChangement) {
        this.taux = taux;
        this.dateChangement = dateChangement;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTaux() {
        return taux;
    }

    public void setTaux(Double taux) {
        this.taux = taux;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "TauxAlerte{" +
                "id=" + id +
                ", taux=" + taux +
                ", dateChangement=" + dateChangement +
                '}';
    }
}
