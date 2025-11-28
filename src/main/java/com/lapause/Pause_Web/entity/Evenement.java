package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    
    @Column(length = 1000) 
    private String description;
    
    private String dateEvenement;
    private double prix;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "evenement_type",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private List<TypeEvenement> types = new ArrayList<>();

    public Evenement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(String dateEvenement) { this.dateEvenement = dateEvenement; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public List<Photo> getPhotos() { return photos; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }
    public List<TypeEvenement> getTypes() { return types; }
    public void setTypes(List<TypeEvenement> types) { this.types = types; }
}