package tn.vermeg.vermegapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.vermeg.vermegapplication.entities.Projet;

public interface ProjetRepository extends JpaRepository<Projet, Long> {
    @Query("SELECT p FROM Projet p WHERE p.nom = ?1")
    Projet findByNom(String nom);

}
