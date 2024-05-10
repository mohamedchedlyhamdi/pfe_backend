package tn.vermeg.vermegapplication.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.vermeg.vermegapplication.JwtUtils;
import tn.vermeg.vermegapplication.entities.Utilisateur;
import tn.vermeg.vermegapplication.Services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<Utilisateur> registerUser(@RequestBody Utilisateur newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Utilisateur savedUser = userService.saveUser(newUser);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        Utilisateur user = userService.findUserByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateJwtToken(user); // Generating token using JwtUtils
            return ResponseEntity.ok("Bearer " + token);
        }
        return ResponseEntity.status(401).body("Login failed: Invalid email or password.");
    }
}
