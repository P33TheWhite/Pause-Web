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

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(Utilisateur utilisateur, Model model) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()) != null) {
            model.addAttribute("error", "Cet email est déjà utilisé !");
            return "auth/register";
        }

        utilisateur.setEstCotisant(false);
        
        utilisateurRepository.save(utilisateur);

        return "redirect:/login?success";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        
        Utilisateur user = utilisateurRepository.findByEmail(email);

        if (user != null && user.getMotDePasse().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Email ou mot de passe incorrect.");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profil")
    public String showProfile(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        
        Utilisateur userAJour = utilisateurRepository.findById(user.getId()).orElse(null);
        model.addAttribute("user", userAJour);
        return "user/profil";
    }

    @PostMapping("/profil/demande-cotisation")
    public String demanderCotisation(@RequestParam String classe, HttpSession session) {
        Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
        Utilisateur user = utilisateurRepository.findById(sessionUser.getId()).orElse(null);
        
        if (user != null) {
            user.setClasse(classe); 
            user.setDemandeCotisationEnCours(true);
            utilisateurRepository.save(user);
            session.setAttribute("user", user);
        }
        return "redirect:/profil?success";
    }
}