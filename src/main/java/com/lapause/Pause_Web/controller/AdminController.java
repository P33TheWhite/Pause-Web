package com.lapause.Pause_Web.controller;

import com.lapause.Pause_Web.entity.*;
import com.lapause.Pause_Web.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UtilisateurRepository utilisateurRepo;
    @Autowired private EvenementRepository eventRepo;
    @Autowired private InscriptionRepository inscriptionRepo;

    @GetMapping
    public String adminDashboard(Model model, HttpSession session) {
        
        List<Utilisateur> demandes = utilisateurRepo.findByDemandeCotisationEnCoursTrue();
        model.addAttribute("demandes", demandes);

        model.addAttribute("evenements", eventRepo.findAll());

        model.addAttribute("tousLesUsers", utilisateurRepo.findAll());

        return "admin/dashboard";
    }

    @PostMapping("/cotisation/{id}/{decision}")
    public String gererCotisation(@PathVariable Long id, @PathVariable String decision) {
        Utilisateur u = utilisateurRepo.findById(id).orElse(null);
        if (u != null) {
            if ("accepter".equals(decision)) {
                u.setEstCotisant(true);
            }
            u.setDemandeCotisationEnCours(false);
            utilisateurRepo.save(u);
        }
        return "redirect:/admin";
    }

    @PostMapping("/user/{id}/toggle-cotisant")
    public String toggleCotisant(@PathVariable Long id) {
        Utilisateur u = utilisateurRepo.findById(id).orElse(null);
        if (u != null) {
            boolean nouveauStatut = !u.isEstCotisant();
            u.setEstCotisant(nouveauStatut);
            
            if (nouveauStatut) {
                u.setDemandeCotisationEnCours(false);
            }
            
            utilisateurRepo.save(u);
        }
        return "redirect:/admin";
    }

    @GetMapping("/event/{id}")
    public String gererEvent(@PathVariable Long id, Model model) {
        Evenement event = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event introuvable"));
        List<Inscription> inscriptions = inscriptionRepo.findByEvenementId(id);

        long nbInscrits = inscriptions.size();
        long nbPaye = inscriptions.stream().filter(Inscription::isaPaye).count();
        long nbRepas = inscriptions.stream().filter(Inscription::isaRecupereRepas).count();

        model.addAttribute("event", event);
        model.addAttribute("inscriptions", inscriptions);
        model.addAttribute("stats", new long[]{nbInscrits, nbPaye, nbRepas});
        
        return "admin/event-detail";
    }
    
    @PostMapping("/inscription/{id}/update")
    public String updateInscription(@PathVariable Long id, 
                                    @RequestParam(required = false) boolean aPaye,
                                    @RequestParam(required = false) boolean aMange) {
        Inscription ins = inscriptionRepo.findById(id).orElse(null);
        if(ins != null) {
            ins.setaPaye(aPaye);
            ins.setaRecupereRepas(aMange);
            inscriptionRepo.save(ins);
            return "redirect:/admin/event/" + ins.getEvenement().getId();
        }
        return "redirect:/admin";
    }


    @GetMapping("/event/new")
    public String formEvent(Model model) {
        model.addAttribute("evenement", new Evenement());
        return "admin/event-form";
    }
    
    @PostMapping("/event/save")
    public String saveEvent(Evenement evenement) {
        eventRepo.save(evenement);
        return "redirect:/admin";
    }
}