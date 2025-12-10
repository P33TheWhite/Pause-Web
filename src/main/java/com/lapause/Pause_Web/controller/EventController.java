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
import java.util.Map;

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
        model.addAttribute("userStaffStatus", eventService.getUserStaffStatus(user, events));
        model.addAttribute("userStaffValidatedStatus", eventService.getUserStaffValidatedStatus(user, events));
        model.addAttribute("userPrices", eventService.getUserPrices(user));

        return "event/agenda";
    }

    @GetMapping("/event/{id}")
    public String eventDetail(@PathVariable Long id, Model model, HttpSession session) {
        Evenement event = eventService.getEventById(id);
        if (event == null)
            return "redirect:/agenda";

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        model.addAttribute("event", event);

        Map<String, Object> stats = eventService.getDetailedStats(id);
        model.addAttribute("nbInscrits", stats.get("nbInscrits"));

        boolean estInscrit = false;
        boolean inscriptionEnAttente = false;
        boolean estStaff = false;
        boolean estStaffValide = false;
        if (user != null) {
            List<com.lapause.Pause_Web.entity.Inscription> inscriptions = eventService.getInscriptionsForEvent(id);
            for (com.lapause.Pause_Web.entity.Inscription ins : inscriptions) {
                if (ins.getUtilisateur().getId().equals(user.getId())) {
                    estInscrit = true;
                    inscriptionEnAttente = ins.isEnAttente();
                    estStaff = ins.isEstStaff();
                    estStaffValide = ins.isStaffValide();
                    break;
                }
            }
        }
        model.addAttribute("estInscrit", estInscrit);
        model.addAttribute("inscriptionEnAttente", inscriptionEnAttente);
        model.addAttribute("estStaff", estStaff);
        model.addAttribute("estStaffValide", estStaffValide);

        return "event/detail";
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

    @PostMapping("/event/{id}/staff")
    public String staffEvent(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        String result = eventService.registerStaff(user, id);
        if (result.contains("validée") || result.contains("mise à jour")) {
            redirectAttributes.addFlashAttribute("success", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        return "redirect:/agenda";
    }

    @PostMapping("/event/{id}/unstaff")
    public String unstaffEvent(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        String result = eventService.removeStaff(user, id);
        redirectAttributes.addFlashAttribute("error", result);

        return "redirect:/agenda";
    }
}