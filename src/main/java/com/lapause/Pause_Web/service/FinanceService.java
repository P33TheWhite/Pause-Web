package com.lapause.Pause_Web.service;

import com.lapause.Pause_Web.entity.Event;
import com.lapause.Pause_Web.entity.Registration;
import com.lapause.Pause_Web.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService {

    @Autowired
    private RegistrationRepository registrationRepo;

    public Map<String, Object> getGlobalStats(List<Event> events) {
        double totalCollected = 0;
        double totalTheoretical = 0;
        double totalExpenses = 0;

        for (Event evt : events) {
            if (evt.getShoppingCost() != null) {
                totalExpenses += evt.getShoppingCost();
            }
            List<Registration> registrations = registrationRepo.findByEventId(evt.getId());
            for (Registration reg : registrations) {
                double price = reg.getAmountToPay() != null ? reg.getAmountToPay() : 0.0;
                totalTheoretical += price;
                if (reg.isHasPaid()) {
                    totalCollected += price;
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecolte", totalCollected);
        stats.put("totalTheorique", totalTheoretical);
        stats.put("totalManquant", totalTheoretical - totalCollected);
        stats.put("totalDepenses", totalExpenses);
        stats.put("benefice", totalCollected - totalExpenses);
        return stats;
    }

    public Map<Long, Map<String, Double>> getEventStats(List<Event> events) {
        Map<Long, Map<String, Double>> eventStats = new HashMap<>();

        for (Event evt : events) {
            List<Registration> registrations = registrationRepo.findByEventId(evt.getId());
            double collected = 0;
            double theoretical = 0;
            double expenses = evt.getShoppingCost() != null ? evt.getShoppingCost() : 0.0;

            for (Registration reg : registrations) {
                double price = reg.getAmountToPay() != null ? reg.getAmountToPay() : 0.0;
                theoretical += price;
                if (reg.isHasPaid()) {
                    collected += price;
                }
            }

            Map<String, Double> stats = new HashMap<>();
            stats.put("recolte", collected);
            stats.put("theorique", theoretical);
            stats.put("depenses", expenses);
            stats.put("benefice", collected - expenses);
            eventStats.put(evt.getId(), stats);
        }
        return eventStats;
    }
}
