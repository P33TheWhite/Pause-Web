package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateur")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "mot_de_passe")
    private String password;

    @Column(name = "nom")
    private String lastName;

    @Column(name = "prenom")
    private String firstName;

    @Column(name = "classe")
    private String studentClass;

    @Column(name = "est_cotisant")
    private boolean isContributor;

    @Column(name = "demande_cotisation_en_cours")
    private boolean isCotisationPending;

    private boolean vip;

    @Column(name = "est_staffeur")
    private boolean isStaff = false;

    private Integer points = 0;

    @Column(name = "points_all_time")
    private Integer allTimePoints = 0;

    @Column(name = "solde_reduction")
    private Double reductionBalance = 0.0;

    private String icon = "default.png";

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "utilisateur_unlocked_icons", joinColumns = @JoinColumn(name = "utilisateur_id"))
    @Column(name = "unlocked_icons")
    private List<String> unlockedIcons = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private OfficeInfo officeInfo;

    public User() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public boolean isContributor() {
        return isContributor;
    }

    public void setContributor(boolean contributor) {
        isContributor = contributor;
    }

    public boolean isCotisationPending() {
        return isCotisationPending;
    }

    public void setCotisationPending(boolean cotisationPending) {
        isCotisationPending = cotisationPending;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean isStaff() {
        return isStaff || officeInfo != null || "Bureau".equalsIgnoreCase(studentClass);
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getAllTimePoints() {
        return allTimePoints;
    }

    public void setAllTimePoints(Integer allTimePoints) {
        this.allTimePoints = allTimePoints;
    }

    public Double getReductionBalance() {
        return reductionBalance;
    }

    public void setReductionBalance(Double reductionBalance) {
        this.reductionBalance = reductionBalance;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getUnlockedIcons() {
        return unlockedIcons;
    }

    public void setUnlockedIcons(List<String> unlockedIcons) {
        this.unlockedIcons = unlockedIcons;
    }

    public OfficeInfo getOfficeInfo() {
        return officeInfo;
    }

    public void setOfficeInfo(OfficeInfo officeInfo) {
        this.officeInfo = officeInfo;
    }
}
