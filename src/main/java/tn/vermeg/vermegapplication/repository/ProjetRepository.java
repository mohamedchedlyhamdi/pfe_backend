package tn.vermeg.vermegapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.vermeg.vermegapplication.entities.Projet;

public interface ProjetRepository extends JpaRepository<Projet, Long> {
    // Add custom query methods if needed
}
