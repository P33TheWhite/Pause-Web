package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.Utilisateur;
import com.lapause.Pause_Web.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // --- PARTIE INSCRIPTION ---

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(Utilisateur utilisateur, Model model) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()) != null) {
            model.addAttribute("error", "Cet email est déjà utilisé !");
            return "auth/register";
        }

        // Par défaut, un nouveau n'est pas cotisant (seul l'admin le validera)
        utilisateur.setEstCotisant(false);
        
        // Sauvegarde en BDD
        utilisateurRepository.save(utilisateur);

        return "redirect:/login?success";
    }

    // --- PARTIE CONNEXION ---

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        
        // On cherche l'utilisateur dans la BDD
        Utilisateur user = utilisateurRepository.findByEmail(email);

        // Vérification basique
        if (user != null && user.getMotDePasse().equals(password)) {
            // SUCCÈS : On stocke l'utilisateur dans la session
            session.setAttribute("user", user);
            return "redirect:/"; // Retour à l'accueil
        } else {
            // ECHEC
            model.addAttribute("error", "Email ou mot de passe incorrect.");
            return "auth/login";
        }
    }

    // --- DECONNEXION ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // On vide la session
        return "redirect:/";
    }
}