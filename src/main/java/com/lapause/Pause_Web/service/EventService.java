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
                    if (u.getPoints() == null)
                        u.setPoints(0);
                    u.setPoints(u.getPoints() + 1);
                    userService.saveUser(u);

                    // Update session if this user is the one logged in
                    Utilisateur sessionUser = (Utilisateur) session.getAttribute("user");
                    if (sessionUser != null && sessionUser.getId().equals(u.getId())) {
                        session.setAttribute("user", u);
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
            inscriptionRepo.save(ins);

            if (ins.getEvenement().isEstArchive()) {
                if (aMange != oldMange) {
                    Utilisateur u = ins.getUtilisateur();
                    if (u.getPoints() == null)
                        u.setPoints(0);

                    if (aMange) {
                        u.setPoints(u.getPoints() + 1);
                    } else {
                        u.setPoints(Math.max(0, u.getPoints() - 1));
                    }
                    userService.saveUser(u);
                }
            }
            return ins.getEvenement().getId();
        }
        return null;
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
        inscription.setEnAttente(waitingList);

        if (waitingList) {
            inscriptionRepo.save(inscription);
            return "Complet ! Vous êtes sur liste d'attente.";
        } else {
            double originalPrice = user.isEstCotisant()
                    ? (event.getPrixCotisant() != null ? event.getPrixCotisant() : 0)
                    : (event.getPrixNonCotisant() != null ? event.getPrixNonCotisant() : 0);

            if (user.getPoints() != null && user.getPoints() > 5) {
                user.setPoints(user.getPoints() - 5);
                userService.saveUser(user);
                double newPrice = Math.max(0, originalPrice - 1.0);
                inscription.setMontantAPayer(newPrice);
                inscriptionRepo.save(inscription);
                return "Inscription validée ! Réduction VIP appliquée (-1€). Nouveau prix : " + newPrice + " €";
            } else {
                inscription.setMontantAPayer(originalPrice);
                inscriptionRepo.save(inscription);
                return "Inscription validée !";
            }
        }
    }

    public String unregisterUserFromEvent(Utilisateur user, Long eventId) {
        Inscription ins = inscriptionRepo.findByUtilisateurIdAndEvenementId(user.getId(), eventId);
        if (ins != null) {
            boolean wasActive = !ins.isEnAttente();
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
            return "Désinscription prise en compte.";
        }
        return "Inscription introuvable.";
    }
}
