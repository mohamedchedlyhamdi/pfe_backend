

package tn.vermeg.vermegapplication.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.vermeg.vermegapplication.entities.Utilisateur;
import tn.vermeg.vermegapplication.repository.UtilisateurRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

@Service
public class UserService {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public UserService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur saveUser(Utilisateur user) {
        // Add additional logic if necessary
        return utilisateurRepository.save(user);
    }

    public Utilisateur findUserByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }



}
