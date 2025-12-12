package com.lapause.Pause_Web.service;

import com.lapause.Pause_Web.entity.Event;
import com.lapause.Pause_Web.entity.Registration;
import com.lapause.Pause_Web.entity.User;
import com.lapause.Pause_Web.repository.EventRepository;
import com.lapause.Pause_Web.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private RegistrationRepository registrationRepo;

    @Autowired
    private UserService userService;

    public List<Event> getAllActiveEvents() {
        return eventRepo.findAll().stream()
                .filter(e -> !e.isArchived())
                .collect(Collectors.toList());
    }

    public List<Event> getAllArchivedEvents() {
        return eventRepo.findAll().stream()
                .filter(Event::isArchived)
                .collect(Collectors.toList());
    }

    public Event getEventById(Long id) {
        return eventRepo.findById(id).orElse(null);
    }

    public void saveEvent(Event event) {
        eventRepo.save(event);
    }

    public void archiveEvent(Long id, jakarta.servlet.http.HttpSession session) {
        Event evt = getEventById(id);
        if (evt != null) {
            if (evt.isArchived()) {
                return;
            }
            evt.setArchived(true);
            saveEvent(evt);

            List<Registration> registrations = registrationRepo.findByEventId(id);
            registrations.sort((r1, r2) -> r1.getRegistrationDate().compareTo(r2.getRegistrationDate()));

            for (int i = 0; i < registrations.size(); i++) {
                Registration reg = registrations.get(i);
                int pointsToAward = 10;
                if (i < 3) {
                    pointsToAward = 50;
                } else if (i < 10) {
                    pointsToAward = 30;
                }

                reg.setEarnedPoints(pointsToAward);
                registrationRepo.save(reg);

                if (reg.isHasMeal()) {
                    User u = reg.getUser();
                    userService.addPoints(u.getId(), pointsToAward);

                    User sessionUser = (User) session.getAttribute("user");
                    if (sessionUser != null && sessionUser.getId().equals(u.getId())) {
                        session.setAttribute("user", userService.getUserById(u.getId()));
                    }
                }
            }
        }
    }

    public Map<Long, String> getEventStats(List<Event> events) {
        Map<Long, String> stats = new HashMap<>();
        for (Event e : events) {
            int count = registrationRepo.findByEventId(e.getId()).size();
            String max = (e.getMaxSpots() != null) ? String.valueOf(e.getMaxSpots()) : "∞";
            stats.put(e.getId(), count + " / " + max);
        }
        return stats;
    }

    public List<Registration> getInscriptionsForEvent(Long eventId) {
        return registrationRepo.findByEventId(eventId);
    }

    public Map<String, Object> getDetailedStats(Long eventId) {
        List<Registration> registrations = getInscriptionsForEvent(eventId);
        long nbInscrits = registrations.size();
        long nbPaye = registrations.stream().filter(Registration::isHasPaid).count();
        long nbRepas = registrations.stream().filter(Registration::isHasMeal).count();

        double recettes = registrations.stream()
                .filter(Registration::isHasPaid)
                .mapToDouble(i -> i.getAmountToPay() != null ? i.getAmountToPay() : 0.0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("nbInscrits", nbInscrits);
        stats.put("nbPaye", nbPaye);
        stats.put("nbRepas", nbRepas);
        stats.put("recettes", recettes);

        return stats;
    }

    public Long updateInscription(Long id, boolean hasPaid, boolean hasMeal) {
        Registration reg = registrationRepo.findById(id).orElse(null);
        if (reg != null) {
            boolean oldMeal = reg.isHasMeal();
            reg.setHasPaid(hasPaid);
            reg.setHasMeal(hasMeal);

            if (reg.getEvent().isArchived()) {
                if (hasMeal != oldMeal) {
                    User u = reg.getUser();
                    int points = reg.getEarnedPoints() != null ? reg.getEarnedPoints() : 10;
                    if (hasMeal) {
                        userService.addPoints(u.getId(), points);
                    } else {
                        userService.addPoints(u.getId(), -points);
                    }
                }
            }

            registrationRepo.save(reg);
            return reg.getEvent().getId();
        }
        return null;
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateEventCost(Long id, Double cost) {
        Event evt = eventRepo.findById(id).orElse(null);
        if (evt != null) {
            evt.setShoppingCost(cost);
            eventRepo.save(evt);
        }
    }

    public Map<Long, Integer> getPlacesRestantes(List<Event> events) {
        Map<Long, Integer> places = new HashMap<>();
        for (Event e : events) {
            long inscrits = registrationRepo.countByEventIdAndIsWaitingFalse(e.getId());
            if (e.getMaxSpots() != null) {
                places.put(e.getId(), (int) Math.max(0, e.getMaxSpots() - inscrits));
            } else {
                places.put(e.getId(), Integer.MAX_VALUE);
            }
        }
        return places;
    }

    public Map<Long, String> getUserStatus(User user, List<Event> events) {
        Map<Long, String> status = new HashMap<>();
        if (user != null) {
            List<Registration> registrations = registrationRepo.findByUserId(user.getId());
            for (Registration reg : registrations) {
                status.put(reg.getEvent().getId(), reg.isWaiting() ? "WAITING" : "REGISTERED");
            }
        }
        for (Event evt : events) {
            status.putIfAbsent(evt.getId(), "NONE");
        }
        return status;
    }

    public Map<Long, Boolean> getUserStaffStatus(User user, List<Event> events) {
        Map<Long, Boolean> staffStatus = new HashMap<>();
        if (user != null) {
            List<Registration> registrations = registrationRepo.findByUserId(user.getId());
            for (Registration reg : registrations) {
                staffStatus.put(reg.getEvent().getId(), reg.isStaff());
            }
        }
        for (Event evt : events) {
            staffStatus.putIfAbsent(evt.getId(), false);
        }
        return staffStatus;
    }

    public Map<Long, Boolean> getUserStaffValidatedStatus(User user, List<Event> events) {
        Map<Long, Boolean> staffValidated = new HashMap<>();
        if (user != null) {
            List<Registration> registrations = registrationRepo.findByUserId(user.getId());
            for (Registration reg : registrations) {
                staffValidated.put(reg.getEvent().getId(), reg.isStaffValidated());
            }
        }
        for (Event evt : events) {
            staffValidated.putIfAbsent(evt.getId(), false);
        }
        return staffValidated;
    }

    public Map<Long, Double> getUserPrices(User user) {
        Map<Long, Double> prices = new HashMap<>();
        if (user != null) {
            List<Registration> registrations = registrationRepo.findByUserId(user.getId());
            for (Registration reg : registrations) {
                if (reg.getAmountToPay() != null) {
                    prices.put(reg.getEvent().getId(), reg.getAmountToPay());
                } else {
                    double price = user.isContributor()
                            ? (reg.getEvent().getMemberPrice() != null ? reg.getEvent().getMemberPrice() : 0)
                            : (reg.getEvent().getNonMemberPrice() != null ? reg.getEvent().getNonMemberPrice() : 0);
                    prices.put(reg.getEvent().getId(), price);
                }
            }
        }
        return prices;
    }

    public void registerUserToEvent(User user, Event event) {
        if (event == null)
            throw new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable");

        boolean already = registrationRepo.findByEventId(event.getId()).stream()
                .anyMatch(i -> i.getUser().getId().equals(user.getId()));
        if (already)
            throw new com.lapause.Pause_Web.exception.PauseWebException("Vous êtes déjà inscrit à cet événement.");

        long currentInscrits = registrationRepo.countByEventIdAndIsWaitingFalse(event.getId());
        boolean waitingList = event.getMaxSpots() != null && currentInscrits >= event.getMaxSpots();

        Registration registration = new Registration(user, event);

        double basePrice = user.isContributor()
                ? (event.getMemberPrice() != null ? event.getMemberPrice() : 0)
                : (event.getNonMemberPrice() != null ? event.getNonMemberPrice() : 0);

        registration.setAmountToPay(basePrice);

        if (waitingList) {
            registration.setWaiting(true);
            registrationRepo.save(registration);
            throw new com.lapause.Pause_Web.exception.PauseWebException(
                    "Complet ! Vous êtes sur liste d'attente. Inscription validée !");
        } else {

            double finalPrice = basePrice;

            if (user.getReductionBalance() != null && user.getReductionBalance() > 0) {
                double reduction = user.getReductionBalance();
                registration.setUsedPoints(5);
                double priceBeforeVoucher = finalPrice;
                finalPrice = Math.max(0, finalPrice - reduction);
                double usedReduction = priceBeforeVoucher - finalPrice;

                user.setReductionBalance(Math.max(0, user.getReductionBalance() - usedReduction));
                registration.setVoucherDiscount(usedReduction);
            }

            userService.saveUser(user);
            registration.setAmountToPay(finalPrice);
            registrationRepo.save(registration);
        }
    }

    public void unregisterUserFromEvent(User user, Event event) {
        if (event == null)
            throw new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable");

        Registration reg = registrationRepo.findByUserIdAndEventId(user.getId(), event.getId());
        if (reg != null) {
            boolean wasActive = !reg.isWaiting();

            if (reg.getUsedPoints() != null && reg.getUsedPoints() > 0) {
                user.setPoints(user.getPoints() + reg.getUsedPoints());
            }

            if (reg.getVoucherDiscount() != null && reg.getVoucherDiscount() > 0) {
                if (user.getReductionBalance() == null)
                    user.setReductionBalance(0.0);
                user.setReductionBalance(user.getReductionBalance() + reg.getVoucherDiscount());
            }

            if (reg.isStaffValidated() && reg.getEarnedPoints() != null) {
                userService.addPoints(user.getId(), -reg.getEarnedPoints());
            }

            userService.saveUser(user);
            registrationRepo.delete(reg);

            if (wasActive) {
                List<Registration> waitingList = registrationRepo
                        .findByEventIdAndIsWaitingTrueOrderByRegistrationDateAsc(event.getId());
                if (!waitingList.isEmpty()) {
                    Registration luckyWinner = waitingList.get(0);
                    luckyWinner.setWaiting(false);
                    registrationRepo.save(luckyWinner);
                }
            }
        } else {
            throw new com.lapause.Pause_Web.exception.PauseWebException("Inscription introuvable.");
        }
    }

    public void registerStaff(User user, Event event) {
        if (event == null)
            throw new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable");

        if (!user.isStaff())
            throw new com.lapause.Pause_Web.exception.PauseWebException("Vous n'êtes pas staffeur.");

        Registration existing = registrationRepo.findByEventId(event.getId()).stream()
                .filter(i -> i.getUser().getId().equals(user.getId()))
                .findFirst().orElse(null);

        if (existing != null) {
            if (existing.isStaff())
                throw new com.lapause.Pause_Web.exception.PauseWebException("Vous êtes déjà staff sur cet événement.");
            existing.setStaff(true);
            existing.setAmountToPay(0.0);
            registrationRepo.save(existing);
        } else {
            Registration registration = new Registration(user, event);
            registration.setStaff(true);
            registration.setAmountToPay(0.0);
            registrationRepo.save(registration);
        }
    }

    public void removeStaff(User user, Event event) {
        if (event == null)
            throw new com.lapause.Pause_Web.exception.PauseWebException("Event introuvable");

        Registration existing = registrationRepo.findByEventId(event.getId()).stream()
                .filter(i -> i.getUser().getId().equals(user.getId()))
                .findFirst().orElse(null);

        if (existing == null)
            throw new com.lapause.Pause_Web.exception.PauseWebException("Inscription introuvable.");
        if (!existing.isStaff())
            throw new com.lapause.Pause_Web.exception.PauseWebException("Vous n'êtes pas staff sur cet événement.");

        if (existing.isStaffValidated()) {
            if (existing.getEarnedPoints() != null) {
                userService.addPoints(user.getId(), -existing.getEarnedPoints());
            }
            existing.setStaffValidated(false);
            existing.setEarnedPoints(null);
        }

        existing.setStaff(false);

        double price = user.isContributor()
                ? (existing.getEvent().getMemberPrice() != null ? existing.getEvent().getMemberPrice() : 0.0)
                : (existing.getEvent().getNonMemberPrice() != null ? existing.getEvent().getNonMemberPrice() : 0.0);

        existing.setAmountToPay(price);
        registrationRepo.save(existing);
    }

    public void validateStaffPoints(Long eventId) {
        List<Registration> staffRegistrations = registrationRepo.findByEventId(eventId).stream()
                .filter(Registration::isStaff)
                .sorted((i1, i2) -> i1.getRegistrationDate().compareTo(i2.getRegistrationDate()))
                .collect(Collectors.toList());

        for (int i = 0; i < staffRegistrations.size(); i++) {
            Registration reg = staffRegistrations.get(i);
            if (!reg.isStaffValidated()) {
                int points = 10;
                if (i < 3)
                    points = 50;
                else if (i < 7)
                    points = 30;

                reg.setStaffValidated(true);
                reg.setEarnedPoints(points);
                registrationRepo.save(reg);

                userService.addPoints(reg.getUser().getId(), points);
            }
        }
    }

    public void removeStaffByAdmin(Long eventId, Long userId) {
        Registration reg = registrationRepo.findByUserIdAndEventId(userId, eventId);
        if (reg != null) {
            registrationRepo.delete(reg);
        }
    }
}
