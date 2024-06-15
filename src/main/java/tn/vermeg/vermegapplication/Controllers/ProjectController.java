package tn.vermeg.vermegapplication.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.vermeg.vermegapplication.entities.Projet;
import tn.vermeg.vermegapplication.entities.ProjetDTO;
import tn.vermeg.vermegapplication.repository.ProjetRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projets")
public class ProjectController {

    private final ProjetRepository projetRepository;

    @Autowired
    public ProjectController(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }

    @PostMapping("/addProject")
    public ResponseEntity<String> addProject(@RequestBody Projet project) {
        project.setDerniereMaj(new Date());
        projetRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body("Project added successfully.");
    }
    @GetMapping("/allProjets")
    public ResponseEntity<List<ProjetDTO>> getAllProjects() {
        List<Projet> projects = projetRepository.findAll();

        // Convert Projet entities to ProjetDTO objects
        List<ProjetDTO> projectDTOs = projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(projectDTOs);
    }

    private ProjetDTO convertToDTO(Projet projet) {
        ProjetDTO projetDTO = new ProjetDTO();
        projetDTO.setId(projet.getId());
        projetDTO.setNom(projet.getNom());
        projetDTO.setNomUtilisateur(projet.getNomUtilisateur());
        projetDTO.setDateEntree(projet.getDateEntree());
        projetDTO.setDerniereMaj(projet.getDerniereMaj());
        projetDTO.setDescription(projet.getDescription());
        projetDTO.setEtat(projet.getEtat());
        return projetDTO;
    }
}

