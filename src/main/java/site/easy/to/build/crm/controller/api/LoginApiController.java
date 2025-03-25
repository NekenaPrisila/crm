package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.UserRepository;

@RestController
@RequestMapping("/api/login")
public class LoginApiController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/check-login")
    public ResponseEntity<Boolean> checkLogin(@RequestBody String email) {
        try {
            // 1. Valider l'email
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(false);
            }

            boolean exists = false;
            User userconnecter = userRepository.findManagerByEmail(email);
            if (userconnecter != null) {
                System.out.println("user " + userconnecter.getEmail());
                exists = true; 
            }
            
            // 3. Retourner le résultat
            return ResponseEntity.ok(exists);
            
        } catch (Exception e) {
            throw e;
            // return ResponseEntity.internalServerError().body(false);
        }
    }
}
