package com.lapause.Pause_Web.service;

import com.lapause.Pause_Web.entity.Evenement;
import com.lapause.Pause_Web.entity.Inscription;
import com.lapause.Pause_Web.repository.EvenementRepository;
import com.lapause.Pause_Web.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService {

    @Autowired
    private EvenementRepository eventRepo;

    @Autowired
    private InscriptionRepository inscriptionRepo;

    public Map<String, Object> getGlobalStats() {
        List<Evenement> events = eventRepo.findAll();
        double totalRecolte = 0;
        double totalTheorique = 0;
        double totalDepenses = 0;

        for (Evenement evt : events) {
            if (evt.getCoutCourses() != null) {
                totalDepenses += evt.getCoutCourses();
            }
            List<Inscription> inscriptions = inscriptionRepo.findByEvenementId(evt.getId());
            for (Inscription ins : inscriptions) {
                double price = ins.getMontantAPayer() != null ? ins.getMontantAPayer() : 0.0;
                totalTheorique += price;
                if (ins.isaPaye()) {
                    totalRecolte += price;
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecolte", totalRecolte);
        stats.put("totalTheorique", totalTheorique);
        stats.put("totalManquant", totalTheorique - totalRecolte);
        stats.put("totalDepenses", totalDepenses);
        stats.put("benefice", totalRecolte - totalDepenses);
        return stats;
    }

    public Map<Long, Map<String, Double>> getEventStats() {
        List<Evenement> events = eventRepo.findAll();
        Map<Long, Map<String, Double>> eventStats = new HashMap<>();

        for (Evenement evt : events) {
            List<Inscription> inscriptions = inscriptionRepo.findByEvenementId(evt.getId());
            double recolte = 0;
            double theorique = 0;
            double depenses = evt.getCoutCourses() != null ? evt.getCoutCourses() : 0.0;

            for (Inscription ins : inscriptions) {
                double price = ins.getMontantAPayer() != null ? ins.getMontantAPayer() : 0.0;
                theorique += price;
                if (ins.isaPaye()) {
                    recolte += price;
                }
            }

            Map<String, Double> stats = new HashMap<>();
            stats.put("recolte", recolte);
            stats.put("theorique", theorique);
            stats.put("depenses", depenses);
            stats.put("benefice", recolte - depenses);
            eventStats.put(evt.getId(), stats);
        }
        return eventStats;
    }
}
