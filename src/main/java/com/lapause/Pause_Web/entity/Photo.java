package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;

@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String titre;

    // Relation N-1 : Plusieurs photos appartiennent à un seul événement
    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    public Photo() {}

    // --- GETTERS ET SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
}