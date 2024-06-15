package tn.vermeg.vermegapplication.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.vermeg.vermegapplication.JwtUtils;
import tn.vermeg.vermegapplication.entities.Utilisateur;
import tn.vermeg.vermegapplication.Services.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")

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
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Utilisateur user = userService.findUserByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateJwtToken(user); // Generating token using JwtUtils
            Map<String, String> response = new HashMap<>();
            response.put("token", "Bearer " + token);
            return ResponseEntity.ok(response);
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Login failed: Invalid email or password.");
        return ResponseEntity.status(401).body(errorResponse);
    }

}
