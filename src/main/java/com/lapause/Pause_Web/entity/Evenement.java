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

    private java.time.LocalDate date;
    private java.time.LocalTime heureDebut;
    private java.time.LocalTime heureFin;

    private Double prixCotisant;
    private Double prixNonCotisant;
    private String lienPaiement;

    private Integer nbPlacesMax;

    private Double coutCourses = 0.0;

    private boolean estArchive = false;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "evenement_type", joinColumns = @JoinColumn(name = "evenement_id"), inverseJoinColumns = @JoinColumn(name = "type_id"))
    private List<TypeEvenement> types = new ArrayList<>();

    public Evenement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.time.LocalDate getDate() {
        return date;
    }

    public void setDate(java.time.LocalDate date) {
        this.date = date;
    }

    public java.time.LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(java.time.LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public java.time.LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(java.time.LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public Double getPrixCotisant() {
        return prixCotisant;
    }

    public void setPrixCotisant(Double prixCotisant) {
        this.prixCotisant = prixCotisant;
    }

    public Double getPrixNonCotisant() {
        return prixNonCotisant;
    }

    public void setPrixNonCotisant(Double prixNonCotisant) {
        this.prixNonCotisant = prixNonCotisant;
    }

    public String getLienPaiement() {
        return lienPaiement;
    }

    public void setLienPaiement(String lienPaiement) {
        this.lienPaiement = lienPaiement;
    }

    public Integer getNbPlacesMax() {
        return nbPlacesMax;
    }

    public void setNbPlacesMax(Integer nbPlacesMax) {
        this.nbPlacesMax = nbPlacesMax;
    }

    public boolean isEstArchive() {
        return estArchive;
    }

    public void setEstArchive(boolean estArchive) {
        this.estArchive = estArchive;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<TypeEvenement> getTypes() {
        return types;
    }

    public void setTypes(List<TypeEvenement> types) {
        this.types = types;
    }

    public Double getCoutCourses() {
        return coutCourses;
    }

    public void setCoutCourses(Double coutCourses) {
        this.coutCourses = coutCourses;
    }
}