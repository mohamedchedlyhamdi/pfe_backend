package tn.vermeg.vermegapplication.Controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.vermeg.vermegapplication.entities.Projet;
import tn.vermeg.vermegapplication.entities.Utilisateur;
import tn.vermeg.vermegapplication.repository.ProjetRepository;
import tn.vermeg.vermegapplication.repository.UtilisateurRepository;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
public class ProjectController {

    private final UtilisateurRepository utilisateurRepository;
    private final ProjetRepository projetRepository;


    private String jwtSecret;

    @Autowired
    public ProjectController(UtilisateurRepository utilisateurRepository, ProjetRepository projetRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.projetRepository = projetRepository;
    }
    @PostMapping("/add")
    public ResponseEntity<String> addProject(@RequestBody Projet project) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is required to add a project.");
        }


        String userEmail = authentication.getName();


        Utilisateur user = utilisateurRepository.findByEmail(userEmail);


        if (user != null && (user.getMission().equals("directeur projet") || user.getMission().equals("chef projet"))) {

            return ResponseEntity.status(HttpStatus.CREATED).body("Project added successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only users with missions 'chef projet' or 'directeur projet' can add projects.");
        }
    }
}
