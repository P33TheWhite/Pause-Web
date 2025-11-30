package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.Utilisateur;
import com.lapause.Pause_Web.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(Utilisateur utilisateur, Model model) {
        Utilisateur registeredUser = userService.registerUser(utilisateur);
        if (registeredUser == null) {
            model.addAttribute("error", "Cet email est déjà utilisé !");
            return "auth/register";
        }
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
        Utilisateur user = userService.authenticate(email, password);
        if (user != null) {
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
        if (user == null)
            return "redirect:/login";

        Utilisateur userAJour = userService.getUserById(user.getId());
        model.addAttribute("user", userAJour);
        return "user/profil";
    }

    @PostMapping("/profil/demande-cotisation")
    public String demanderCotisation(@RequestParam String classe, HttpSession session) {
        Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
        if (sessionUser != null) {
            userService.requestCotisation(sessionUser.getId(), classe);
            session.setAttribute("user", userService.getUserById(sessionUser.getId()));
        }
        return "redirect:/profil?success";
    }

    @PostMapping("/profil/change-password")
    public String changePassword(@RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
        if (sessionUser == null)
            return "redirect:/login";

        String result = userService.changePassword(sessionUser.getId(), oldPassword, newPassword, confirmPassword);
        if ("success".equals(result)) {
            redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        return "redirect:/profil";
    }
}