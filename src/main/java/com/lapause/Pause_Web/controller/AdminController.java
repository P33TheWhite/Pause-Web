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
    public String showFinance(Model model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false, defaultValue = "all") String status) {

        // Get all events first
        List<Evenement> allEvents = eventService.getAllActiveEvents();
        allEvents.addAll(eventService.getAllArchivedEvents());

        // Filter the list of events to display AND calculate stats
        List<Evenement> displayedEvents = allEvents.stream()
                .filter(e -> {
                    boolean matchYear = (year == null || e.getDate().getYear() == year);
                    boolean matchEvent = (eventId == null || e.getId().equals(eventId));
                    boolean matchStatus = true;

                    if ("archived".equals(status)) {
                        matchStatus = e.isEstArchive();
                    } else if ("active".equals(status)) {
                        matchStatus = !e.isEstArchive();
                    }
                    // if "all", matchStatus remains true

                    return matchYear && matchEvent && matchStatus;
                })
                .toList();

        Map<String, Object> globalStats = financeService.getGlobalStats(displayedEvents);
        Map<Long, Map<String, Double>> eventStats = financeService.getEventStats(displayedEvents);

        List<String> labels = new java.util.ArrayList<>();
        List<Double> dataRecolte = new java.util.ArrayList<>();
        List<Double> dataTheorique = new java.util.ArrayList<>();
        List<Double> dataDepenses = new java.util.ArrayList<>();
        List<Double> dataBenefice = new java.util.ArrayList<>();

        for (Evenement evt : displayedEvents) {
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

        // Get unique years for the filter dropdown
        List<Integer> years = allEvents.stream()
                .map(e -> e.getDate().getYear())
                .distinct()
                .sorted(java.util.Comparator.reverseOrder())
                .toList();

        model.addAttribute("globalStats", globalStats);
        model.addAttribute("events", displayedEvents);
        model.addAttribute("eventStats", eventStats);
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartRecolte", dataRecolte);
        model.addAttribute("chartTheorique", dataTheorique);
        model.addAttribute("chartDepenses", dataDepenses);
        model.addAttribute("chartBenefice", dataBenefice);

        // Filter attributes
        model.addAttribute("allEvents", allEvents);
        model.addAttribute("years", years);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedEventId", eventId);
        model.addAttribute("selectedStatus", status);

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

    @DeleteMapping("/event/{id}/archive")
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
    public String gererEvent(@PathVariable Long id, @RequestParam(required = false) String source, Model model) {
        Evenement event = eventService.getEventById(id);
        if (event == null)
            throw new RuntimeException("Event introuvable");

        model.addAttribute("event", event);
        model.addAttribute("event", event);
        List<com.lapause.Pause_Web.entity.Inscription> allInscriptions = eventService.getInscriptionsForEvent(id);

        List<com.lapause.Pause_Web.entity.Inscription> staffInscriptions = allInscriptions.stream()
                .filter(com.lapause.Pause_Web.entity.Inscription::isEstStaff)
                .sorted((i1, i2) -> i1.getDateInscription().compareTo(i2.getDateInscription()))
                .toList();

        model.addAttribute("inscriptions", allInscriptions);
        model.addAttribute("staffInscriptions", staffInscriptions);
        model.addAttribute("stats", eventService.getDetailedStats(id));

        String backLink = "finance".equals(source) ? "/admin/finance" : "/admin";
        model.addAttribute("backLink", backLink);

        return "admin/event-detail";
    }

    @PostMapping("/inscription/{id}/update")
    public String updateInscription(@PathVariable Long id,
            @RequestParam(required = false) boolean aPaye,
            @RequestParam(required = false) boolean aMange,
            @RequestParam(required = false) String source) {
        Long eventId = eventService.updateInscription(id, aPaye, aMange);
        if (eventId != null) {
            String redirect = "redirect:/admin/event/" + eventId;
            if (source != null && !source.isEmpty()) {
                redirect += "?source=" + source;
            }
            return redirect;
        }
        return "redirect:/admin";
    }

    @PutMapping("/event/{id}/update-cost")
    public String updateCost(@PathVariable Long id, @RequestParam Double coutCourses,
            @RequestParam(required = false) String source) {
        eventService.updateEventCost(id, coutCourses);
        String redirect = "redirect:/admin/event/" + id;
        if (source != null && !source.isEmpty()) {
            redirect += "?source=" + source;
        }
        return redirect;
    }

    @PostMapping("/event/{id}/validate-staff")
    public String validateStaff(@PathVariable Long id) {
        eventService.validateStaffPoints(id);
        return "redirect:/admin/event/" + id;
    }

    @PostMapping("/event/{id}/staff/{userId}/remove")
    public String removeStaffByAdmin(@PathVariable Long id, @PathVariable Long userId) {
        eventService.removeStaffByAdmin(id, userId);
        return "redirect:/admin/event/" + id;
    }

    @Autowired
    private com.lapause.Pause_Web.repository.TypeEvenementRepository typeRepo;

    @GetMapping("/event/new")
    public String formEvent(Model model) {
        model.addAttribute("evenement", new Evenement());
        model.addAttribute("allTypes", typeRepo.findAll());
        return "admin/event-form";
    }

    @GetMapping("/event/{id}/edit")
    public String editEvent(@PathVariable Long id, Model model) {
        Evenement evt = eventService.getEventById(id);
        if (evt == null)
            throw new RuntimeException("Event not found");
        model.addAttribute("evenement", evt);
        model.addAttribute("allTypes", typeRepo.findAll());
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