package com.lapause.Pause_Web.service;

import com.lapause.Pause_Web.entity.User;
import com.lapause.Pause_Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("null")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }
        user.setContributor(false);
        user.getUnlockedIcons().add("default.png");
        return userRepository.save(user);
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void requestCotisation(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.setCotisationPending(true);
            userRepository.save(user);
        }
    }

    public String changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = getUserById(userId);
        if (user == null)
            return "User not found";

        if (!user.getPassword().equals(oldPassword)) {
            return "L'ancien mot de passe est incorrect.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Les nouveaux mots de passe ne correspondent pas.";
        }
        if (oldPassword.equals(newPassword)) {
            return "Le nouveau mot de passe doit être différent de l'ancien.";
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        return "success";
    }

    public List<User> getPendingCotisationRequests() {
        return userRepository.findByIsCotisationPendingTrue();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void manageCotisation(Long userId, String decision) {
        User user = getUserById(userId);
        if (user != null) {
            if ("accepter".equals(decision)) {
                user.setContributor(true);
            }
            user.setCotisationPending(false);
            userRepository.save(user);
        }
    }

    public void toggleCotisant(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            boolean newStatus = !user.isContributor();
            user.setContributor(newStatus);
            if (newStatus) {
                user.setCotisationPending(false);
            }
            userRepository.save(user);
        }
    }

    public void toggleVip(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.setVip(!user.isVip());
            userRepository.save(user);
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean buyItem(Long userId, String itemId, int cost) {
        User user = getUserById(userId);
        if (user != null && user.getPoints() >= cost) {
            user.setPoints(user.getPoints() - cost);

            if (itemId.equals("croque")) {
                // Logic for croque
            } else if (itemId.startsWith("reduction_")) {
                double amount = 0.0;
                if (itemId.equals("reduction_1"))
                    amount = 1.0;
                else if (itemId.equals("reduction_2"))
                    amount = 2.0;
                else if (itemId.equals("reduction_5"))
                    amount = 5.0;

                if (user.getReductionBalance() == null)
                    user.setReductionBalance(0.0);
                user.setReductionBalance(user.getReductionBalance() + amount);
            } else {
                if (!user.getUnlockedIcons().contains(itemId)) {
                    user.getUnlockedIcons().add(itemId);
                }
            }

            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean equipIcon(Long userId, String iconName) {
        User user = getUserById(userId);
        if (user != null && (user.getUnlockedIcons().contains(iconName) || iconName.equals("default.png"))) {
            user.setIcon(iconName);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public List<User> getLeaderboard() {
        return userRepository.findAllByOrderByAllTimePointsDesc();
    }

    public void addPoints(Long userId, int pointsToAdd) {
        User user = getUserById(userId);
        if (user != null) {
            user.setPoints(user.getPoints() + pointsToAdd);
            user.setAllTimePoints(user.getAllTimePoints() + pointsToAdd);
            userRepository.save(user);
        }
    }

    public void becomeStaff(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.setStaff(true);
            userRepository.save(user);
        }
    }
}
