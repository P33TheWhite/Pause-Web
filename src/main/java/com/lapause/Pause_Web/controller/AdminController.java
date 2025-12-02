package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.Evenement;
import com.lapause.Pause_Web.entity.Photo;
import com.lapause.Pause_Web.service.EventService;
import com.lapause.Pause_Web.service.FileStorageService;
import com.lapause.Pause_Web.service.FinanceService;
import com.lapause.Pause_Web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/finance")
    public String showFinance(Model model) {
        Map<String, Object> globalStats = financeService.getGlobalStats();
        Map<Long, Map<String, Double>> eventStats = financeService.getEventStats();
        List<Evenement> events = eventService.getAllActiveEvents();
        events.addAll(eventService.getAllArchivedEvents());

        List<String> labels = new java.util.ArrayList<>();
        List<Double> dataRecolte = new java.util.ArrayList<>();
        List<Double> dataTheorique = new java.util.ArrayList<>();
        List<Double> dataDepenses = new java.util.ArrayList<>();
        List<Double> dataBenefice = new java.util.ArrayList<>();

        for (Evenement evt : events) {
            labels.add(evt.getTitre());
            Map<String, Double> stats = eventStats.get(evt.getId());
            if (stats != null) {
                dataRecolte.add(stats.get("recolte"));
                dataTheorique.add(stats.get("theorique"));
                dataDepenses.add(stats.get("depenses"));
                dataBenefice.add(stats.get("benefice"));
            } else {
                dataRecolte.add(0.0);
                dataTheorique.add(0.0);
                dataDepenses.add(0.0);
                dataBenefice.add(0.0);
            }
        }

        model.addAttribute("globalStats", globalStats);
        model.addAttribute("events", events);
        model.addAttribute("eventStats", eventStats);
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartRecolte", dataRecolte);
        model.addAttribute("chartTheorique", dataTheorique);
        model.addAttribute("chartDepenses", dataDepenses);
        model.addAttribute("chartBenefice", dataBenefice);

        return "admin/finance";
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("demandes", userService.getPendingCotisationRequests());
        List<Evenement> events = eventService.getAllActiveEvents();
        model.addAttribute("evenements", events);
        model.addAttribute("eventStats", eventService.getEventStats(events));
        model.addAttribute("tousLesUsers", userService.getAllUsers());
        return "admin/dashboard";
    }

    @GetMapping("/archives")
    public String adminArchives(Model model) {
        model.addAttribute("archives", eventService.getAllArchivedEvents());
        return "admin/archives";
    }

    @PostMapping("/event/{id}/archive")
    public String archiveEvent(@PathVariable Long id, jakarta.servlet.http.HttpSession session) {
        eventService.archiveEvent(id, session);
        return "redirect:/admin";
    }

    @PostMapping("/cotisation/{id}/{decision}")
    public String gererCotisation(@PathVariable Long id, @PathVariable String decision) {
        userService.manageCotisation(id, decision);
        return "redirect:/admin";
    }

    @PostMapping("/user/{id}/toggle-cotisant")
    public String toggleCotisant(@PathVariable Long id) {
        userService.toggleCotisant(id);
        return "redirect:/admin";
    }

    @PostMapping("/user/{id}/toggle-vip")
    public String toggleVip(@PathVariable Long id) {
        userService.toggleVip(id);
        return "redirect:/admin";
    }

    @GetMapping("/event/{id}")
    public String gererEvent(@PathVariable Long id, Model model) {
        Evenement event = eventService.getEventById(id);
        if (event == null)
            throw new RuntimeException("Event introuvable");

        model.addAttribute("event", event);
        model.addAttribute("inscriptions", eventService.getInscriptionsForEvent(id));
        model.addAttribute("stats", eventService.getDetailedStats(id));
        return "admin/event-detail";
    }

    @PostMapping("/inscription/{id}/update")
    public String updateInscription(@PathVariable Long id,
            @RequestParam(required = false) boolean aPaye,
            @RequestParam(required = false) boolean aMange) {
        Long eventId = eventService.updateInscription(id, aPaye, aMange);
        if (eventId != null) {
            return "redirect:/admin/event/" + eventId;
        }
        return "redirect:/admin";
    }

    @PostMapping("/event/{id}/update-cost")
    public String updateCost(@PathVariable Long id, @RequestParam Double coutCourses) {
        eventService.updateEventCost(id, coutCourses);
        return "redirect:/admin/event/" + id;
    }

    @GetMapping("/event/new")
    public String formEvent(Model model) {
        model.addAttribute("evenement", new Evenement());
        return "admin/event-form";
    }

    @GetMapping("/event/{id}/edit")
    public String editEvent(@PathVariable Long id, Model model) {
        Evenement evt = eventService.getEventById(id);
        if (evt == null)
            throw new RuntimeException("Event not found");
        model.addAttribute("evenement", evt);
        return "admin/event-form";
    }

    @PostMapping("/event/save")
    public String saveEvent(Evenement evenement,
            @RequestParam("image") MultipartFile multipartFile) throws IOException {
        String imageUrl = fileStorageService.saveFile(multipartFile);
        if (imageUrl != null) {
            Photo p = new Photo();
            p.setUrl(imageUrl);
            p.setTitre("Affiche");
            p.setEvenement(evenement);
            evenement.setPhotos(List.of(p));
        }
        eventService.saveEvent(evenement);
        return "redirect:/admin";
    }
}