package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.Event;
import com.lapause.Pause_Web.entity.Photo;
import com.lapause.Pause_Web.entity.Registration;
import com.lapause.Pause_Web.service.EventService;
import com.lapause.Pause_Web.service.FileStorageService;
import com.lapause.Pause_Web.service.FinanceService;
import com.lapause.Pause_Web.service.UserService;
import com.lapause.Pause_Web.repository.EventTypeRepository;
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

    @Autowired
    private EventTypeRepository eventTypeRepo;

    @GetMapping("/finance")
    public String showFinance(Model model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false, defaultValue = "all") String status) {

        List<Event> allEvents = eventService.getAllActiveEvents();
        allEvents.addAll(eventService.getAllArchivedEvents());

        List<Event> displayedEvents = allEvents.stream()
                .filter(e -> {
                    boolean matchYear = (year == null || (e.getDate() != null && e.getDate().getYear() == year));
                    boolean matchEvent = (eventId == null || e.getId().equals(eventId));
                    boolean matchStatus = true;

                    if ("archived".equals(status)) {
                        matchStatus = e.isArchived();
                    } else if ("active".equals(status)) {
                        matchStatus = !e.isArchived();
                    }

                    return matchYear && matchEvent && matchStatus;
                })
                .toList();

        Map<String, Object> globalStats = financeService.getGlobalStats(displayedEvents);
        Map<Long, Map<String, Double>> eventStats = financeService.getEventStats(displayedEvents);

        List<String> labels = new java.util.ArrayList<>();
        List<Double> dataCollected = new java.util.ArrayList<>();
        List<Double> dataTheoretical = new java.util.ArrayList<>();
        List<Double> dataExpenses = new java.util.ArrayList<>();
        List<Double> dataProfit = new java.util.ArrayList<>();

        for (Event evt : displayedEvents) {
            labels.add(evt.getTitle());
            Map<String, Double> stats = eventStats.get(evt.getId());
            if (stats != null) {
                dataCollected.add(stats.get("recolte"));
                dataTheoretical.add(stats.get("theorique"));
                dataExpenses.add(stats.get("depenses"));
                dataProfit.add(stats.get("benefice"));
            } else {
                dataCollected.add(0.0);
                dataTheoretical.add(0.0);
                dataExpenses.add(0.0);
                dataProfit.add(0.0);
            }
        }

        List<Integer> years = allEvents.stream()
                .filter(e -> e.getDate() != null)
                .map(e -> e.getDate().getYear())
                .distinct()
                .sorted(java.util.Comparator.reverseOrder())
                .toList();

        model.addAttribute("globalStats", globalStats);
        model.addAttribute("events", displayedEvents);
        model.addAttribute("eventStats", eventStats);
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartRecolte", dataCollected);
        model.addAttribute("chartTheorique", dataTheoretical);
        model.addAttribute("chartDepenses", dataExpenses);
        model.addAttribute("chartBenefice", dataProfit);

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
        List<Event> events = eventService.getAllActiveEvents();
        model.addAttribute("evenements", events); // Keeping "evenements" for view compatibility for now, or change to
                                                  // "events"? View likely uses "evenements".
        model.addAttribute("events", events); // Adding "events" too just in case.
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
    public String manageCotisation(@PathVariable Long id, @PathVariable String decision) {
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
    public String manageEvent(@PathVariable Long id, @RequestParam(required = false) String source, Model model) {
        Event event = eventService.getEventById(id);
        if (event == null)
            throw new RuntimeException("Event introuvable");

        model.addAttribute("event", event);
        List<Registration> allRegistrations = eventService.getInscriptionsForEvent(id);

        List<Registration> staffRegistrations = allRegistrations.stream()
                .filter(Registration::isStaff)
                .sorted((r1, r2) -> r1.getRegistrationDate().compareTo(r2.getRegistrationDate()))
                .toList();

        model.addAttribute("inscriptions", allRegistrations);
        model.addAttribute("staffInscriptions", staffRegistrations);
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
    public String updateCost(@PathVariable Long id, @RequestParam Double shoppingCost,
            @RequestParam(required = false) String source) {
        eventService.updateEventCost(id, shoppingCost);
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

    @GetMapping("/event/new")
    public String formEvent(Model model) {
        model.addAttribute("evenement", new Event());
        model.addAttribute("allTypes", eventTypeRepo.findAll());
        return "admin/event-form";
    }

    @GetMapping("/event/{id}/edit")
    public String editEvent(@PathVariable Long id, Model model) {
        Event evt = eventService.getEventById(id);
        if (evt == null)
            throw new RuntimeException("Event not found");
        model.addAttribute("event", evt);
        model.addAttribute("allTypes", eventTypeRepo.findAll());
        return "admin/event-form";
    }

    @PostMapping("/event/save")
    public String saveEvent(@ModelAttribute("event") Event event,
            @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {

        Event eventToSave;

        if (event.getId() != null) {
            Event existingEvent = eventService.getEventById(event.getId());
            if (existingEvent != null) {
                existingEvent.setTitle(event.getTitle());
                existingEvent.setDescription(event.getDescription());
                existingEvent.setDate(event.getDate());
                existingEvent.setStartTime(event.getStartTime());
                existingEvent.setEndTime(event.getEndTime());
                existingEvent.setMemberPrice(event.getMemberPrice());
                existingEvent.setNonMemberPrice(event.getNonMemberPrice());
                existingEvent.setPaymentLink(event.getPaymentLink());
                existingEvent.setMaxSpots(event.getMaxSpots());
                existingEvent.setTypes(event.getTypes());

                eventToSave = existingEvent;
            } else {
                eventToSave = event;
            }
        } else {
            eventToSave = event;
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(multipartFile);
            if (imageUrl != null) {
                Photo p = new Photo();
                p.setUrl(imageUrl);
                p.setTitle("Affiche");
                p.setEvent(eventToSave);

                eventToSave.setPhotos(List.of(p));
            }
        }

        eventService.saveEvent(eventToSave);
        return "redirect:/admin";
    }
}