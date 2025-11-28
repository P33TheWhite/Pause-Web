package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;

@Entity
public class InfoBureau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String poste;
    private String bio;
    private String photoUrl;

    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    public InfoBureau() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}