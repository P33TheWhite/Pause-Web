package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evenement")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titre")
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "date")
    private java.time.LocalDate date;

    @Column(name = "heure_debut")
    private java.time.LocalTime startTime;

    @Column(name = "heure_fin")
    private java.time.LocalTime endTime;

    @Column(name = "prix_cotisant")
    private Double memberPrice;

    @Column(name = "prix_non_cotisant")
    private Double nonMemberPrice;

    @Column(name = "lien_paiement")
    private String paymentLink;

    @Column(name = "nb_places_max")
    private Integer maxSpots;

    @Column(name = "cout_courses")
    private Double shoppingCost = 0.0;

    @Column(name = "est_archive")
    private boolean isArchived = false;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "evenement_type", joinColumns = @JoinColumn(name = "evenement_id"), inverseJoinColumns = @JoinColumn(name = "type_id"))
    private List<EventType> types = new ArrayList<>();

    public Event() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public java.time.LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }

    public java.time.LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(java.time.LocalTime endTime) {
        this.endTime = endTime;
    }

    public Double getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Double memberPrice) {
        this.memberPrice = memberPrice;
    }

    public Double getNonMemberPrice() {
        return nonMemberPrice;
    }

    public void setNonMemberPrice(Double nonMemberPrice) {
        this.nonMemberPrice = nonMemberPrice;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public Integer getMaxSpots() {
        return maxSpots;
    }

    public void setMaxSpots(Integer maxSpots) {
        this.maxSpots = maxSpots;
    }

    public Double getShoppingCost() {
        return shoppingCost;
    }

    public void setShoppingCost(Double shoppingCost) {
        this.shoppingCost = shoppingCost;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<EventType> getTypes() {
        return types;
    }

    public void setTypes(List<EventType> types) {
        this.types = types;
    }
}
