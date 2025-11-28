package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TypeEvenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle; 

    @ManyToMany(mappedBy = "types")
    private List<Evenement> evenements = new ArrayList<>();

    public TypeEvenement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public List<Evenement> getEvenements() { return evenements; }
    public void setEvenements(List<Evenement> evenements) { this.evenements = evenements; }
}