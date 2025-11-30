package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean aPaye;
    private boolean aRecupereRepas;
    private boolean enAttente = false;
    private LocalDateTime dateInscription = LocalDateTime.now();

    private Double montantAPayer;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    public Inscription() {
    }

    public Inscription(Utilisateur u, Evenement e) {
        this.utilisateur = u;
        this.evenement = e;
        this.aPaye = false;
        this.aRecupereRepas = false;
        this.enAttente = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isaPaye() {
        return aPaye;
    }

    public void setaPaye(boolean aPaye) {
        this.aPaye = aPaye;
    }

    public boolean isaRecupereRepas() {
        return aRecupereRepas;
    }

    public void setaRecupereRepas(boolean aRecupereRepas) {
        this.aRecupereRepas = aRecupereRepas;
    }

    public boolean isEnAttente() {
        return enAttente;
    }

    public void setEnAttente(boolean enAttente) {
        this.enAttente = enAttente;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public Double getMontantAPayer() {
        return montantAPayer;
    }

    public void setMontantAPayer(Double montantAPayer) {
        this.montantAPayer = montantAPayer;
    }
}