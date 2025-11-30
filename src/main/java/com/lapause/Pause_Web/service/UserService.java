package com.lapause.Pause_Web.service;

import com.lapause.Pause_Web.entity.Utilisateur;
import com.lapause.Pause_Web.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur registerUser(Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()) != null) {
            return null;
        }
        utilisateur.setEstCotisant(false);
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur authenticate(String email, String password) {
        Utilisateur user = utilisateurRepository.findByEmail(email);
        if (user != null && user.getMotDePasse().equals(password)) {
            return user;
        }
        return null;
    }

    public Utilisateur getUserById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public void requestCotisation(Long userId, String classe) {
        Utilisateur user = getUserById(userId);
        if (user != null) {
            user.setClasse(classe);
            user.setDemandeCotisationEnCours(true);
            utilisateurRepository.save(user);
        }
    }

    public String changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        Utilisateur user = getUserById(userId);
        if (user == null)
            return "User not found";

        if (!user.getMotDePasse().equals(oldPassword)) {
            return "L'ancien mot de passe est incorrect.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Les nouveaux mots de passe ne correspondent pas.";
        }
        if (oldPassword.equals(newPassword)) {
            return "Le nouveau mot de passe doit être différent de l'ancien.";
        }

        user.setMotDePasse(newPassword);
        utilisateurRepository.save(user);
        return "success";
    }

    public List<Utilisateur> getPendingCotisationRequests() {
        return utilisateurRepository.findByDemandeCotisationEnCoursTrue();
    }

    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }

    public void manageCotisation(Long userId, String decision) {
        Utilisateur user = getUserById(userId);
        if (user != null) {
            if ("accepter".equals(decision)) {
                user.setEstCotisant(true);
            }
            user.setDemandeCotisationEnCours(false);
            utilisateurRepository.save(user);
        }
    }

    public void toggleCotisant(Long userId) {
        Utilisateur user = getUserById(userId);
        if (user != null) {
            boolean newStatus = !user.isEstCotisant();
            user.setEstCotisant(newStatus);
            if (newStatus) {
                user.setDemandeCotisationEnCours(false);
            }
            utilisateurRepository.save(user);
        }
    }

    public void saveUser(Utilisateur user) {
        utilisateurRepository.save(user);
    }
}
