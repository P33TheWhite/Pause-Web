package com.lapause.Pause_Web.init;

import com.lapause.Pause_Web.entity.*;
import com.lapause.Pause_Web.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitEvent {

    @Bean
    public CommandLineRunner demo(EvenementRepository eventRepo,
            TypeEvenementRepository typeRepo,
            UtilisateurRepository userRepo,
            InscriptionRepository inscriptionRepo) {
        return (args) -> {
            if (typeRepo.count() == 0) {

                TypeEvenement tSoiree = new TypeEvenement();
                tSoiree.setLibelle("Soirée");
                TypeEvenement tGastro = new TypeEvenement();
                tGastro.setLibelle("Gastronomie");
                TypeEvenement tHumanitaire = new TypeEvenement();
                tHumanitaire.setLibelle("Humanitaire");

                typeRepo.saveAll(List.of(tSoiree, tGastro, tHumanitaire));

                Evenement e1 = new Evenement();
                e1.setTitre("Soirée Intégration");
                e1.setDescription("La plus grosse soirée de l'année pour accueillir les 1A !");
                e1.setDate(java.time.LocalDate.of(2025, 9, 15));
                e1.setHeureDebut(java.time.LocalTime.of(21, 0));
                e1.setHeureFin(java.time.LocalTime.of(4, 0));
                e1.setPrixCotisant(8.0);
                e1.setPrixNonCotisant(12.0);
                e1.setLienPaiement("https://paypal.me/lapause/soiree");
                e1.setTypes(List.of(tSoiree));

                Evenement e2 = new Evenement();
                e2.setTitre("Dégustation Croque-Monsieur");
                e2.setDescription("Venez goûter nos nouvelles recettes au fromage de chèvre.");
                e2.setDate(java.time.LocalDate.of(2025, 10, 2));
                e2.setHeureDebut(java.time.LocalTime.of(12, 0));
                e2.setHeureFin(java.time.LocalTime.of(14, 0));
                e2.setPrixCotisant(2.0);
                e2.setPrixNonCotisant(3.0);
                e2.setTypes(List.of(tGastro));

                eventRepo.saveAll(List.of(e1, e2));

                Utilisateur admin = new Utilisateur();
                admin.setEmail("admin@lapause.com");
                admin.setMotDePasse("admin");
                admin.setNom("Super");
                admin.setPrenom("Admin");
                admin.setClasse("Bureau");
                admin.setEstCotisant(true);

                Utilisateur paul = new Utilisateur();
                paul.setEmail("paul@test.com");
                paul.setMotDePasse("paul");
                paul.setNom("Durand");
                paul.setPrenom("Paul");
                paul.setClasse("ING1");
                paul.setEstCotisant(false);

                Utilisateur lucie = new Utilisateur();
                lucie.setEmail("lucie@test.com");
                lucie.setMotDePasse("lucie");
                lucie.setNom("Martin");
                lucie.setPrenom("Lucie");
                lucie.setClasse("ING2");
                lucie.setEstCotisant(false);
                lucie.setDemandeCotisationEnCours(true);

                userRepo.saveAll(List.of(admin, paul, lucie));

                Inscription i1 = new Inscription(paul, e1);
                i1.setaPaye(true);
                i1.setaRecupereRepas(true);

                Inscription i2 = new Inscription(lucie, e1);
                i2.setaPaye(false);
                i2.setaRecupereRepas(false);

                Inscription i3 = new Inscription(admin, e2);
                i3.setaPaye(true);

                inscriptionRepo.saveAll(List.of(i1, i2, i3));

            }
        };
    }
}