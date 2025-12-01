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

@Controller
public class ShopController {

    @Autowired
    private UserService userService;

    @GetMapping("/shop")
    public String shop(HttpSession session, Model model) {
        Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
        if (sessionUser == null) {
            System.out.println("DEBUG: User session is null in /shop. Redirecting to login.");
            return "redirect:/login";
        }
        System.out.println("DEBUG: User found in session: " + sessionUser.getEmail());
        Utilisateur user = userService.getUserById(sessionUser.getId());
        if (user == null) {
            System.out.println("DEBUG: User found in session but not in DB (ID: " + sessionUser.getId() + ")");
            // Optional: invalidate session if user not found in DB
            session.invalidate();
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "shop/shop";
    }

    @PostMapping("/shop/buy")
    public String buyItem(@RequestParam String itemId, @RequestParam int cost, HttpSession session, Model model) {
        Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        Long userId = sessionUser.getId();
        boolean success = userService.buyItem(userId, itemId, cost);
        if (success) {
            model.addAttribute("message", "Achat effectué avec succès !");
            // Update session user to reflect points change
            Utilisateur updatedUser = userService.getUserById(userId);
            session.setAttribute("user", updatedUser);
        } else {
            model.addAttribute("error", "Points insuffisants ou erreur.");
        }
        Utilisateur user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "shop/shop";
    }

    @PostMapping("/shop/equip")
    public String equipIcon(@RequestParam String iconName, HttpSession session, Model model) {
        Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        Long userId = sessionUser.getId();
        boolean success = userService.equipIcon(userId, iconName);
        if (success) {
            model.addAttribute("message", "Icône équipée !");
            // Update session user to reflect icon change immediately in header if header
            // uses session user
            sessionUser.setIcon(iconName);
            session.setAttribute("user", sessionUser);
        } else {
            model.addAttribute("error", "Erreur lors de l'équipement.");
        }
        Utilisateur user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "shop/shop";
    }
}
