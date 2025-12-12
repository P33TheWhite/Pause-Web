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
public class DataInit {

    @Bean
    @SuppressWarnings("null")
    public CommandLineRunner demo(EventRepository eventRepo,
            EventTypeRepository typeRepo,
            UserRepository userRepo,
            RegistrationRepository registrationRepo) {
        return (args) -> {
            if (typeRepo.count() == 0) {

                EventType tSoiree = new EventType();
                tSoiree.setLabel("Soirée");
                EventType tGastro = new EventType();
                tGastro.setLabel("Gastronomie");
                EventType tHumanitaire = new EventType();
                tHumanitaire.setLabel("Humanitaire");
                EventType tSport = new EventType();
                tSport.setLabel("Sport");
                EventType tCulture = new EventType();
                tCulture.setLabel("Culture");

                typeRepo.saveAll(List.of(tSoiree, tGastro, tHumanitaire, tSport, tCulture));

                Event eHalloween = new Event();
                eHalloween.setTitle("Soirée Halloween");
                eHalloween.setDescription("La soirée la plus effrayante de l'année ! Concours de déguisements.");
                eHalloween.setDate(LocalDate.now().minusMonths(1));
                eHalloween.setStartTime(LocalTime.of(21, 0));
                eHalloween.setEndTime(LocalTime.of(4, 0));
                eHalloween.setMemberPrice(10.0);
                eHalloween.setNonMemberPrice(15.0);
                eHalloween.setPaymentLink("https://tinyurl.com/3d8z9ft9");
                eHalloween.setShoppingCost(150.50);
                eHalloween.setArchived(true);
                eHalloween.setTypes(List.of(tSoiree));

                Photo pHalloween = new Photo();
                pHalloween.setUrl("/images/events/halloween.png");
                pHalloween.setTitle("Affiche Halloween");
                pHalloween.setEvent(eHalloween);
                eHalloween.setPhotos(List.of(pHalloween));

                Event eCrepes = new Event();
                eCrepes.setTitle("Vente de Crêpes");
                eCrepes.setDescription("Des crêpes chaudes pour le goûter, venez nombreux !");
                eCrepes.setDate(LocalDate.now());
                eCrepes.setStartTime(LocalTime.of(14, 0));
                eCrepes.setEndTime(LocalTime.of(17, 0));
                eCrepes.setMemberPrice(1.0);
                eCrepes.setNonMemberPrice(2.0);
                eCrepes.setPaymentLink("https://tinyurl.com/3d8z9ft9");
                eCrepes.setShoppingCost(30.0);
                eCrepes.setTypes(List.of(tGastro));

                Photo pCrepes = new Photo();
                pCrepes.setUrl("/images/events/crepes.png");
                pCrepes.setTitle("Affiche Crêpes");
                pCrepes.setEvent(eCrepes);
                eCrepes.setPhotos(List.of(pCrepes));

                Event eGala = new Event();
                eGala.setTitle("Gala d'Hiver");
                eGala.setDescription("Une soirée prestigieuse pour célébrer la fin du semestre.");
                eGala.setDate(LocalDate.now().plusMonths(1));
                eGala.setStartTime(LocalTime.of(20, 0));
                eGala.setEndTime(LocalTime.of(2, 0));
                eGala.setMemberPrice(25.0);
                eGala.setNonMemberPrice(35.0);
                eGala.setPaymentLink("https://tinyurl.com/3d8z9ft9");
                eGala.setShoppingCost(500.0);
                eGala.setTypes(List.of(tSoiree));

                Photo pGala = new Photo();
                pGala.setUrl("/images/events/gala.png");
                pGala.setTitle("Affiche Gala");
                pGala.setEvent(eGala);
                eGala.setPhotos(List.of(pGala));

                eventRepo.saveAll(List.of(eHalloween, eCrepes, eGala));

                User admin = new User();
                admin.setEmail("admin@lapause.com");
                admin.setPassword("admin");
                admin.setLastName("Super");
                admin.setFirstName("Admin");
                admin.setStudentClass("Bureau");
                admin.setContributor(true);
                admin.setPoints(1500);
                admin.setAllTimePoints(1500);

                User tresorier = new User();
                tresorier.setEmail("tresorier@lapause.com");
                tresorier.setPassword("tresorier");
                tresorier.setLastName("Picsou");
                tresorier.setFirstName("Balthazar");
                tresorier.setStudentClass("Bureau");
                tresorier.setContributor(true);
                tresorier.setPoints(800);
                tresorier.setAllTimePoints(800);

                User alice = new User();
                alice.setEmail("alice@cy-tech.fr");
                alice.setPassword("alice");
                alice.setLastName("Dupont");
                alice.setFirstName("Alice");
                alice.setStudentClass("ING1");
                alice.setContributor(true);
                alice.setPoints(120);
                alice.setAllTimePoints(120);

                User bob = new User();
                bob.setEmail("bob@cy-tech.fr");
                bob.setPassword("bob");
                bob.setLastName("Marley");
                bob.setFirstName("Bob");
                bob.setStudentClass("ING2");
                bob.setContributor(false);
                bob.setCotisationPending(true);
                bob.setPoints(50);
                bob.setAllTimePoints(50);

                User charlie = new User();
                charlie.setEmail("charlie@cy-tech.fr");
                charlie.setPassword("charlie");
                charlie.setLastName("Chaplin");
                charlie.setFirstName("Charlie");
                charlie.setStudentClass("ING3");
                charlie.setContributor(true);
                charlie.setPoints(300);
                charlie.setAllTimePoints(300);

                User david = new User();
                david.setEmail("david@cy-tech.fr");
                david.setPassword("david");
                david.setLastName("Bowie");
                david.setFirstName("David");
                david.setStudentClass("PREPA2");
                david.setContributor(false);
                david.setPoints(0);
                david.setAllTimePoints(0);

                User eva = new User();
                eva.setEmail("eva@cy-tech.fr");
                eva.setPassword("eva");
                eva.setLastName("Green");
                eva.setFirstName("Eva");
                eva.setStudentClass("ING1");
                eva.setContributor(true);
                eva.setPoints(450);
                eva.setAllTimePoints(450);

                User frank = new User();
                frank.setEmail("frank@cy-tech.fr");
                frank.setPassword("frank");
                frank.setLastName("Sinatra");
                frank.setFirstName("Frank");
                frank.setStudentClass("ING2");
                frank.setContributor(true);
                frank.setVip(true);
                frank.setPoints(600);
                frank.setAllTimePoints(600);

                User grace = new User();
                grace.setEmail("grace@cy-tech.fr");
                grace.setPassword("grace");
                grace.setLastName("Kelly");
                grace.setFirstName("Grace");
                grace.setStudentClass("PREPA1");
                grace.setContributor(false);
                grace.setPoints(10);
                grace.setAllTimePoints(10);

                User harry = new User();
                harry.setEmail("harry@cy-tech.fr");
                harry.setPassword("harry");
                harry.setLastName("Potter");
                harry.setFirstName("Harry");
                harry.setStudentClass("ING1");
                harry.setContributor(true);
                harry.setVip(true);
                harry.setPoints(200);
                harry.setAllTimePoints(200);

                userRepo.saveAll(List.of(admin, tresorier, alice, bob, charlie, david, eva, frank, grace, harry));

                registrationRepo.save(createRegistration(alice, eHalloween, true, true));
                registrationRepo.save(createRegistration(bob, eHalloween, true, true));
                registrationRepo.save(createRegistration(charlie, eHalloween, false, false));
                registrationRepo.save(createRegistration(eva, eHalloween, true, true));

                registrationRepo.save(createRegistration(admin, eCrepes, true, false));
                registrationRepo.save(createRegistration(alice, eCrepes, true, false));
                registrationRepo.save(createRegistration(david, eCrepes, false, false));
                registrationRepo.save(createRegistration(frank, eCrepes, true, true));

                registrationRepo.save(createRegistration(frank, eGala, true, false));
                registrationRepo.save(createRegistration(eva, eGala, true, false));
                registrationRepo.save(createRegistration(tresorier, eGala, true, false));
                registrationRepo.save(createRegistration(harry, eGala, false, false));

            }
        };
    }

    private Registration createRegistration(User u, Event e, boolean hasPaid, boolean hasMeal) {
        Registration i = new Registration(u, e);
        i.setHasPaid(hasPaid);
        i.setHasMeal(hasMeal);

        double price = u.isContributor()
                ? (e.getMemberPrice() != null ? e.getMemberPrice() : 0.0)
                : (e.getNonMemberPrice() != null ? e.getNonMemberPrice() : 0.0);
        i.setAmountToPay(price);

        return i;
    }
}
