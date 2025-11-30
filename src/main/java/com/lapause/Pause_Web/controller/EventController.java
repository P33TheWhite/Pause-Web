package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.Evenement;
import com.lapause.Pause_Web.entity.Utilisateur;
import com.lapause.Pause_Web.service.EventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/agenda")
    public String showAgenda(Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        List<Evenement> events = eventService.getAllActiveEvents();

        model.addAttribute("events", events);
        model.addAttribute("placesRestantes", eventService.getPlacesRestantes(events));
        model.addAttribute("userStatus", eventService.getUserStatus(user, events));
        model.addAttribute("userPrices", eventService.getUserPrices(user));

        return "event/agenda";
    }

    @PostMapping("/event/{id}/register")
    public String registerToEvent(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String result = eventService.registerUserToEvent(user, id);
        if (result.startsWith("Complet") || result.startsWith("Inscription")) {
            redirectAttributes.addFlashAttribute("success", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        return "redirect:/agenda";
    }

    @PostMapping("/event/{id}/unregister")
    public String unregisterFromEvent(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        String result = eventService.unregisterUserFromEvent(user, id);
        redirectAttributes.addFlashAttribute("success", result);

        return "redirect:/agenda";
    }
}