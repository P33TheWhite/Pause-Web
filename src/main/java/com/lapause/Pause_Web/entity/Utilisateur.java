package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String motDePasse;
    private String nom;
    private String prenom;
    private boolean estCotisant; 

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private InfoBureau infoBureau;

    public Utilisateur() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public boolean isEstCotisant() { return estCotisant; }
    public void setEstCotisant(boolean estCotisant) { this.estCotisant = estCotisant; }
    public InfoBureau getInfoBureau() { return infoBureau; }
    public void setInfoBureau(InfoBureau infoBureau) { this.infoBureau = infoBureau; }
}