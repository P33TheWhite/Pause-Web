package com.lapause.Pause_Web.init;

import com.lapause.Pause_Web.entity.*;
import com.lapause.Pause_Web.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
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
                TypeEvenement tSport = new TypeEvenement();
                tSport.setLibelle("Sport");
                TypeEvenement tCulture = new TypeEvenement();
                tCulture.setLibelle("Culture");

                typeRepo.saveAll(List.of(tSoiree, tGastro, tHumanitaire, tSport, tCulture));

                Evenement eHalloween = new Evenement();
                eHalloween.setTitre("Soirée Halloween");
                eHalloween.setDescription("La soirée la plus effrayante de l'année ! Concours de déguisements.");
                eHalloween.setDate(LocalDate.now().minusMonths(1));
                eHalloween.setHeureDebut(LocalTime.of(21, 0));
                eHalloween.setHeureFin(LocalTime.of(4, 0));
                eHalloween.setPrixCotisant(10.0);
                eHalloween.setPrixNonCotisant(15.0);
                eHalloween.setLienPaiement("https://tinyurl.com/3d8z9ft9");
                eHalloween.setTypes(List.of(tSoiree));

                Evenement eCrepes = new Evenement();
                eCrepes.setTitre("Vente de Crêpes");
                eCrepes.setDescription("Des crêpes chaudes pour le goûter, venez nombreux !");
                eCrepes.setDate(LocalDate.now());
                eCrepes.setHeureDebut(LocalTime.of(14, 0));
                eCrepes.setHeureFin(LocalTime.of(17, 0));
                eCrepes.setPrixCotisant(1.0);
                eCrepes.setPrixNonCotisant(2.0);
                eCrepes.setLienPaiement("https://tinyurl.com/3d8z9ft9");
                eCrepes.setTypes(List.of(tGastro));

                Evenement eGala = new Evenement();
                eGala.setTitre("Gala d'Hiver");
                eGala.setDescription("Une soirée prestigieuse pour célébrer la fin du semestre.");
                eGala.setDate(LocalDate.now().plusMonths(1));
                eGala.setHeureDebut(LocalTime.of(20, 0));
                eGala.setHeureFin(LocalTime.of(2, 0));
                eGala.setPrixCotisant(25.0);
                eGala.setPrixNonCotisant(35.0);
                eGala.setLienPaiement("https://tinyurl.com/3d8z9ft9");
                eGala.setTypes(List.of(tSoiree));

                Evenement eBeerPong = new Evenement();
                eBeerPong.setTitre("Tournoi de Beer Pong");
                eBeerPong.setDescription("Montrez votre adresse ! Places très limitées.");
                eBeerPong.setDate(LocalDate.now().plusWeeks(2));
                eBeerPong.setHeureDebut(LocalTime.of(18, 0));
                eBeerPong.setHeureFin(LocalTime.of(23, 0));
                eBeerPong.setPrixCotisant(5.0);
                eBeerPong.setPrixNonCotisant(7.0);
                eBeerPong.setNbPlacesMax(16);
                eBeerPong.setLienPaiement("https://tinyurl.com/3d8z9ft9");
                eBeerPong.setTypes(List.of(tSoiree, tSport));

                Evenement eMusee = new Evenement();
                eMusee.setTitre("Sortie au Louvre");
                eMusee.setDescription("Culture et histoire au programme.");
                eMusee.setDate(LocalDate.now().plusWeeks(3));
                eMusee.setHeureDebut(LocalTime.of(10, 0));
                eMusee.setHeureFin(LocalTime.of(16, 0));
                eMusee.setPrixCotisant(0.0);
                eMusee.setPrixNonCotisant(5.0);
                eMusee.setLienPaiement("https://tinyurl.com/3d8z9ft9");
                eMusee.setTypes(List.of(tCulture));

                eventRepo.saveAll(List.of(eHalloween, eCrepes, eGala, eBeerPong, eMusee));

                Utilisateur admin = new Utilisateur();
                admin.setEmail("admin@lapause.com");
                admin.setMotDePasse("admin");
                admin.setNom("Super");
                admin.setPrenom("Admin");
                admin.setClasse("Bureau");
                admin.setEstCotisant(true);
                admin.setPoints(1500);
                admin.setPointsAllTime(1500);

                Utilisateur tresorier = new Utilisateur();
                tresorier.setEmail("tresorier@lapause.com");
                tresorier.setMotDePasse("tresorier");
                tresorier.setNom("Picsou");
                tresorier.setPrenom("Balthazar");
                tresorier.setClasse("Bureau");
                tresorier.setEstCotisant(true);
                tresorier.setPoints(800);
                tresorier.setPointsAllTime(800);

                Utilisateur alice = new Utilisateur();
                alice.setEmail("alice@cy-tech.fr");
                alice.setMotDePasse("alice");
                alice.setNom("Dupont");
                alice.setPrenom("Alice");
                alice.setClasse("ING1");
                alice.setEstCotisant(true);
                alice.setPoints(120);
                alice.setPointsAllTime(120);

                Utilisateur bob = new Utilisateur();
                bob.setEmail("bob@cy-tech.fr");
                bob.setMotDePasse("bob");
                bob.setNom("Marley");
                bob.setPrenom("Bob");
                bob.setClasse("ING2");
                bob.setEstCotisant(false);
                bob.setDemandeCotisationEnCours(true);
                bob.setPoints(50);
                bob.setPointsAllTime(50);

                Utilisateur charlie = new Utilisateur();
                charlie.setEmail("charlie@cy-tech.fr");
                charlie.setMotDePasse("charlie");
                charlie.setNom("Chaplin");
                charlie.setPrenom("Charlie");
                charlie.setClasse("ING3");
                charlie.setEstCotisant(true);
                charlie.setPoints(300);
                charlie.setPointsAllTime(300);

                Utilisateur david = new Utilisateur();
                david.setEmail("david@cy-tech.fr");
                david.setMotDePasse("david");
                david.setNom("Bowie");
                david.setPrenom("David");
                david.setClasse("PREPA2");
                david.setEstCotisant(false);
                david.setPoints(0);
                david.setPointsAllTime(0);

                Utilisateur eva = new Utilisateur();
                eva.setEmail("eva@cy-tech.fr");
                eva.setMotDePasse("eva");
                eva.setNom("Green");
                eva.setPrenom("Eva");
                eva.setClasse("ING1");
                eva.setEstCotisant(true);
                eva.setPoints(450);
                eva.setPointsAllTime(450);

                Utilisateur frank = new Utilisateur();
                frank.setEmail("frank@cy-tech.fr");
                frank.setMotDePasse("frank");
                frank.setNom("Sinatra");
                frank.setPrenom("Frank");
                frank.setClasse("ING2");
                frank.setEstCotisant(true);
                frank.setVip(true);
                frank.setPoints(600);
                frank.setPointsAllTime(600);

                Utilisateur grace = new Utilisateur();
                grace.setEmail("grace@cy-tech.fr");
                grace.setMotDePasse("grace");
                grace.setNom("Kelly");
                grace.setPrenom("Grace");
                grace.setClasse("PREPA1");
                grace.setEstCotisant(false);
                grace.setPoints(10);
                grace.setPointsAllTime(10);

                Utilisateur harry = new Utilisateur();
                harry.setEmail("harry@cy-tech.fr");
                harry.setMotDePasse("harry");
                harry.setNom("Potter");
                harry.setPrenom("Harry");
                harry.setClasse("ING1");
                harry.setEstCotisant(true);
                harry.setVip(true);
                harry.setPoints(200);
                harry.setPointsAllTime(200);

                userRepo.saveAll(List.of(admin, tresorier, alice, bob, charlie, david, eva, frank, grace, harry));

                inscriptionRepo.save(createInscription(alice, eHalloween, true, true));
                inscriptionRepo.save(createInscription(bob, eHalloween, true, true));
                inscriptionRepo.save(createInscription(charlie, eHalloween, false, false));
                inscriptionRepo.save(createInscription(eva, eHalloween, true, true));

                inscriptionRepo.save(createInscription(admin, eCrepes, true, false));
                inscriptionRepo.save(createInscription(alice, eCrepes, true, false));
                inscriptionRepo.save(createInscription(david, eCrepes, false, false));

                inscriptionRepo.save(createInscription(frank, eGala, true, false));
                inscriptionRepo.save(createInscription(eva, eGala, true, false));
                inscriptionRepo.save(createInscription(tresorier, eGala, true, false));

                inscriptionRepo.save(createInscription(bob, eBeerPong, true, false));
                inscriptionRepo.save(createInscription(charlie, eBeerPong, false, false));

                inscriptionRepo.save(createInscription(grace, eMusee, false, false));

            }
        };
    }

    private Inscription createInscription(Utilisateur u, Evenement e, boolean aPaye, boolean aRecupere) {
        Inscription i = new Inscription(u, e);
        i.setaPaye(aPaye);
        i.setaRecupereRepas(aRecupere);
        return i;
    }
}