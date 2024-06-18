package tn.vermeg.vermegapplication.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.vermeg.vermegapplication.entities.Utilisateur;
import tn.vermeg.vermegapplication.repository.UtilisateurRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public UserService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur saveUser(Utilisateur user) {
        return utilisateurRepository.save(user);
    }

    public Utilisateur findUserByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUserById(Long id) {
        Optional<Utilisateur> optionalUser = utilisateurRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public void deleteUserById(Long id) {
        utilisateurRepository.deleteById(id);
    }
}
