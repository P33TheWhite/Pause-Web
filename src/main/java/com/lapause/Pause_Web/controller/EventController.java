package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.Event;
import com.lapause.Pause_Web.entity.Registration;
import com.lapause.Pause_Web.entity.User;
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
import java.util.Optional;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/agenda")
    public String showAgenda(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Event> events = eventService.getAllActiveEvents();

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
        Event event = eventService.getEventById(id);
        if (event == null)
            return "redirect:/agenda";

        User user = (User) session.getAttribute("user");
        model.addAttribute("event", event);

        Map<String, Object> stats = eventService.getDetailedStats(id);
        model.addAttribute("nbInscrits", stats.get("nbInscrits"));

        boolean isRegistered = false;
        boolean isWaiting = false;
        boolean isStaff = false;
        boolean isStaffValidated = false;

        if (user != null) {
            List<Registration> registrations = eventService.getInscriptionsForEvent(id);
            for (Registration reg : registrations) {
                if (reg.getUser().getId().equals(user.getId())) {
                    isRegistered = true;
                    isWaiting = reg.isWaiting();
                    isStaff = reg.isStaff();
                    isStaffValidated = reg.isStaffValidated();
                    break;
                }
            }
        }
        model.addAttribute("estInscrit", isRegistered);
        model.addAttribute("inscriptionEnAttente", isWaiting);
        model.addAttribute("estStaff", isStaff);
        model.addAttribute("estStaffValide", isStaffValidated);

        return "event/detail";
    }

    @PostMapping("/event/{id}/register")
    public String registerToEvent(@PathVariable("id") Optional<Event> eventOpt,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Event event = eventOpt
                    .orElseThrow(() -> new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable"));

            eventService.registerUserToEvent(user, event);

            redirectAttributes.addFlashAttribute("success", "Inscription réussie !");

        } catch (com.lapause.Pause_Web.exception.PauseWebException e) {
            if (e.getMessage().contains("liste d'attente")) {
                redirectAttributes.addFlashAttribute("success", e.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur technique est survenue.");
        }

        return "redirect:/agenda";
    }

    @PostMapping("/event/{id}/unregister")
    public String unregisterFromEvent(@PathVariable("id") Optional<Event> eventOpt,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            Event event = eventOpt
                    .orElseThrow(() -> new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable"));
            eventService.unregisterUserFromEvent(user, event);
            redirectAttributes.addFlashAttribute("success",
                    "Désinscription prise en compte. Points et réductions remboursés.");
        } catch (com.lapause.Pause_Web.exception.PauseWebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur technique est survenue.");
        }

        return "redirect:/agenda";
    }

    @PostMapping("/event/{id}/staff")
    public String staffEvent(@PathVariable("id") Optional<Event> eventOpt,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            Event event = eventOpt
                    .orElseThrow(() -> new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable"));
            eventService.registerStaff(user, event);
            redirectAttributes.addFlashAttribute("success", "Inscription Staff validée !");
        } catch (com.lapause.Pause_Web.exception.PauseWebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur technique est survenue.");
        }

        return "redirect:/agenda";
    }

    @PostMapping("/event/{id}/unstaff")
    public String unstaffEvent(@PathVariable("id") Optional<Event> eventOpt,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            Event event = eventOpt
                    .orElseThrow(() -> new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable"));
            eventService.removeStaff(user, event);
            redirectAttributes.addFlashAttribute("success", "Vous n'êtes plus staff sur cet événement.");
        } catch (com.lapause.Pause_Web.exception.PauseWebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur technique est survenue.");
        }

        return "redirect:/agenda";
    }
}