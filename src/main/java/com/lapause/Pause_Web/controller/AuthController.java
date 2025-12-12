package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.User;
import com.lapause.Pause_Web.service.UserService;
import com.lapause.Pause_Web.repository.RegistrationRepository;
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

    @Autowired
    private RegistrationRepository registrationRepo;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(User user, Model model) {
        User registeredUser = userService.registerUser(user);
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
        User user = userService.authenticate(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/agenda";
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
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        User updatedUser = userService.getUserById(user.getId());
        model.addAttribute("user", updatedUser);
        model.addAttribute("inscriptions", registrationRepo.findByUserId(user.getId()));
        return "user/profil";
    }

    @PostMapping("/profil/demande-cotisation")
    public String requestCotisation(HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            userService.requestCotisation(sessionUser.getId());
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
        User sessionUser = (User) session.getAttribute("user");
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

    @PostMapping("/profil/join-staff")
    public String joinStaff(HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            userService.becomeStaff(sessionUser.getId());
            session.setAttribute("user", userService.getUserById(sessionUser.getId()));
            redirectAttributes.addFlashAttribute("success", "Félicitations ! Vous avez rejoint l'équipe Staff.");
        }
        return "redirect:/";
    }
}