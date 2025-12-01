package com.lapause.Pause_Web.service;

import com.lapause.Pause_Web.entity.Evenement;
import com.lapause.Pause_Web.entity.Inscription;
import com.lapause.Pause_Web.entity.Utilisateur;
import com.lapause.Pause_Web.repository.EvenementRepository;
import com.lapause.Pause_Web.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EvenementRepository eventRepo;

    @Autowired
    private InscriptionRepository inscriptionRepo;

    @Autowired
    private UserService userService;

    public List<Evenement> getAllActiveEvents() {
        return eventRepo.findAll().stream()
                .filter(e -> !e.isEstArchive())
                .collect(Collectors.toList());
    }

    public List<Evenement> getAllArchivedEvents() {
        return eventRepo.findAll().stream()
                .filter(Evenement::isEstArchive)
                .collect(Collectors.toList());
    }

    public Evenement getEventById(Long id) {
        return eventRepo.findById(id).orElse(null);
    }

    public void saveEvent(Evenement event) {
        eventRepo.save(event);
    }

    public void archiveEvent(Long id, jakarta.servlet.http.HttpSession session) {
        Evenement evt = getEventById(id);
        if (evt != null) {
            if (evt.isEstArchive()) {
                return;
            }
            evt.setEstArchive(true);
            saveEvent(evt);

            List<Inscription> inscriptions = inscriptionRepo.findByEvenementId(id);
            for (Inscription ins : inscriptions) {
                if (ins.isaRecupereRepas()) {
                    Utilisateur u = ins.getUtilisateur();
                    userService.addPoints(u.getId(), 1);

                    Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
                    if (sessionUser != null && sessionUser.getId().equals(u.getId())) {
                        session.setAttribute("user", userService.getUserById(u.getId()));
                    }
                }
            }
        }
    }

    public Map<Long, String> getEventStats(List<Evenement> events) {
        Map<Long, String> stats = new HashMap<>();
        for (Evenement e : events) {
            int count = inscriptionRepo.findByEvenementId(e.getId()).size();
            String max = (e.getNbPlacesMax() != null) ? String.valueOf(e.getNbPlacesMax()) : "∞";
            stats.put(e.getId(), count + " / " + max);
        }
        return stats;
    }

    public List<Inscription> getInscriptionsForEvent(Long eventId) {
        return inscriptionRepo.findByEvenementId(eventId);
    }

    public long[] getDetailedStats(Long eventId) {
        List<Inscription> inscriptions = getInscriptionsForEvent(eventId);
        long nbInscrits = inscriptions.size();
        long nbPaye = inscriptions.stream().filter(Inscription::isaPaye).count();
        long nbRepas = inscriptions.stream().filter(Inscription::isaRecupereRepas).count();
        return new long[] { nbInscrits, nbPaye, nbRepas };
    }

    public Long updateInscription(Long id, boolean aPaye, boolean aMange) {
        Inscription ins = inscriptionRepo.findById(id).orElse(null);
        if (ins != null) {
            boolean oldMange = ins.isaRecupereRepas();
            ins.setaPaye(aPaye);
            ins.setaRecupereRepas(aMange);

            if (ins.getEvenement().isEstArchive()) {
                if (aMange != oldMange) {
                    Utilisateur u = ins.getUtilisateur();
                    if (aMange) {
                        userService.addPoints(u.getId(), 1);
                    } else {
                        userService.addPoints(u.getId(), -1);
                    }
                }
            }

            inscriptionRepo.save(ins);
            return ins.getEvenement().getId();
        }
        return null;
    }

    public void updateEventCost(Long id, Double cost) {
        Evenement evt = eventRepo.findById(id).orElse(null);
        if (evt != null) {
            evt.setCoutCourses(cost);
            eventRepo.save(evt);
        }
    }

    public Map<Long, Integer> getPlacesRestantes(List<Evenement> events) {
        Map<Long, Integer> places = new HashMap<>();
        for (Evenement e : events) {
            long inscrits = inscriptionRepo.countByEvenementIdAndEnAttenteFalse(e.getId());
            if (e.getNbPlacesMax() != null) {
                places.put(e.getId(), (int) Math.max(0, e.getNbPlacesMax() - inscrits));
            } else {
                places.put(e.getId(), Integer.MAX_VALUE);
            }
        }
        return places;
    }

    public Map<Long, String> getUserStatus(Utilisateur user, List<Evenement> events) {
        Map<Long, String> status = new HashMap<>();
        if (user != null) {
            List<Inscription> inscriptions = inscriptionRepo.findByUtilisateurId(user.getId());
            for (Inscription ins : inscriptions) {
                status.put(ins.getEvenement().getId(), ins.isEnAttente() ? "WAITING" : "REGISTERED");
            }
        }
        for (Evenement evt : events) {
            status.putIfAbsent(evt.getId(), "NONE");
        }
        return status;
    }

    public Map<Long, Double> getUserPrices(Utilisateur user) {
        Map<Long, Double> prices = new HashMap<>();
        if (user != null) {
            List<Inscription> inscriptions = inscriptionRepo.findByUtilisateurId(user.getId());
            for (Inscription ins : inscriptions) {
                if (ins.getMontantAPayer() != null) {
                    prices.put(ins.getEvenement().getId(), ins.getMontantAPayer());
                } else {
                    double price = user.isEstCotisant()
                            ? (ins.getEvenement().getPrixCotisant() != null ? ins.getEvenement().getPrixCotisant() : 0)
                            : (ins.getEvenement().getPrixNonCotisant() != null ? ins.getEvenement().getPrixNonCotisant()
                                    : 0);
                    prices.put(ins.getEvenement().getId(), price);
                }
            }
        }
        return prices;
    }

    public String registerUserToEvent(Utilisateur user, Long eventId) {
        Evenement event = getEventById(eventId);
        if (event == null)
            return "Event not found";

        boolean already = inscriptionRepo.findByEvenementId(eventId).stream()
                .anyMatch(i -> i.getUtilisateur().getId().equals(user.getId()));
        if (already)
            return "Vous êtes déjà inscrit à cet événement.";

        long currentInscrits = inscriptionRepo.countByEvenementIdAndEnAttenteFalse(eventId);
        boolean waitingList = event.getNbPlacesMax() != null && currentInscrits >= event.getNbPlacesMax();

        Inscription inscription = new Inscription(user, event);

        double prixBase = user.isEstCotisant()
                ? (event.getPrixCotisant() != null ? event.getPrixCotisant() : 0)
                : (event.getPrixNonCotisant() != null ? event.getPrixNonCotisant() : 0);

        String initialMessage = "Inscription validée !";
        if (user.getPoints() != null && user.getPoints() >= 5) {
            // Logic moved to else block for actual registration
        }
        inscription.setMontantAPayer(prixBase);

        if (waitingList) {
            inscription.setEnAttente(true);
            inscriptionRepo.save(inscription);
            return "Complet ! Vous êtes sur liste d'attente. " + initialMessage;
        } else {

            double finalPrice = user.isEstCotisant()
                    ? (event.getPrixCotisant() != null ? event.getPrixCotisant() : 0)
                    : (event.getPrixNonCotisant() != null ? event.getPrixNonCotisant() : 0);

            StringBuilder message = new StringBuilder("Inscription validée !");

            if (user.getSoldeReduction() != null && user.getSoldeReduction() > 0) {
                double reduction = user.getSoldeReduction();
                inscription.setPointsUtilises(5);
                double priceBeforeVoucher = finalPrice;
                finalPrice = Math.max(0, finalPrice - reduction);
                double usedReduction = priceBeforeVoucher - finalPrice;

                user.setSoldeReduction(Math.max(0, user.getSoldeReduction() - usedReduction));
                inscription.setMontantReductionVoucher(usedReduction);
                message.append(" Réduction Boutique appliquée (-").append(String.format("%.2f", usedReduction))
                        .append("€).");
            }

            userService.saveUser(user);
            inscription.setMontantAPayer(finalPrice);
            inscriptionRepo.save(inscription);

            message.append(" Nouveau prix : ").append(String.format("%.2f", finalPrice)).append(" €");
            return message.toString();
        }
    }

    public String unregisterUserFromEvent(Utilisateur user, Long eventId) {
        Inscription ins = inscriptionRepo.findByUtilisateurIdAndEvenementId(user.getId(), eventId);
        if (ins != null) {
            boolean wasActive = !ins.isEnAttente();

            if (ins.getPointsUtilises() != null && ins.getPointsUtilises() > 0) {
                user.setPoints(user.getPoints() + ins.getPointsUtilises());
            }

            if (ins.getMontantReductionVoucher() != null && ins.getMontantReductionVoucher() > 0) {
                if (user.getSoldeReduction() == null)
                    user.setSoldeReduction(0.0);
                user.setSoldeReduction(user.getSoldeReduction() + ins.getMontantReductionVoucher());
            }
            userService.saveUser(user);

            inscriptionRepo.delete(ins);

            if (wasActive) {
                List<Inscription> waitingList = inscriptionRepo
                        .findByEvenementIdAndEnAttenteTrueOrderByDateInscriptionAsc(eventId);
                if (!waitingList.isEmpty()) {
                    Inscription luckyWinner = waitingList.get(0);
                    luckyWinner.setEnAttente(false);
                    inscriptionRepo.save(luckyWinner);
                }
            }
            return "Désinscription prise en compte. Points et réductions remboursés.";
        }
        return "Inscription introuvable.";
    }
}
