package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Utilisateur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String classe; // "Ing1"
    private boolean estCotisant; // Pour la gestion Admin
    private boolean demandeCotisationEnCours;

    private Integer points = 0;
    private Integer pointsAllTime = 0;

    private Double soldeReduction = 0.0;

    private String icon = "default.png"; // Icone par défaut

    @ElementCollection(fetch = FetchType.EAGER)
    private java.util.List<String> unlockedIcons = new java.util.ArrayList<>();

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private InfoBureau infoBureau;

    public Utilisateur() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isEstCotisant() {
        return estCotisant;
    }

    public void setEstCotisant(boolean estCotisant) {
        this.estCotisant = estCotisant;
    }

    public InfoBureau getInfoBureau() {
        return infoBureau;
    }

    public void setInfoBureau(InfoBureau infoBureau) {
        this.infoBureau = infoBureau;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public boolean isDemandeCotisationEnCours() {
        return demandeCotisationEnCours;
    }

    public void setDemandeCotisationEnCours(boolean demandeCotisationEnCours) {
        this.demandeCotisationEnCours = demandeCotisationEnCours;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPointsAllTime() {
        return pointsAllTime;
    }

    public void setPointsAllTime(Integer pointsAllTime) {
        this.pointsAllTime = pointsAllTime;
    }

    public Double getSoldeReduction() {
        return soldeReduction;
    }

    public void setSoldeReduction(Double soldeReduction) {
        this.soldeReduction = soldeReduction;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public java.util.List<String> getUnlockedIcons() {
        return unlockedIcons;
    }

    public void setUnlockedIcons(java.util.List<String> unlockedIcons) {
        this.unlockedIcons = unlockedIcons;
    }
}