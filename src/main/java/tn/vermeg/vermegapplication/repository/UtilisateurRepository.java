package tn.vermeg.vermegapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.vermeg.vermegapplication.entities.Utilisateur;

import java.util.List;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    // Add custom query methods if needed
    Utilisateur findByEmail(String email);

    List<Utilisateur> findByMissionIn(String... missions);
}
