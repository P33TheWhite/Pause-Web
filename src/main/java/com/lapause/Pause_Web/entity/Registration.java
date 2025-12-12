package com.lapause.Pause_Web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscription")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "a_paye")
    private boolean hasPaid;

    @Column(name = "a_recupere_repas")
    private boolean hasMeal;

    @Column(name = "en_attente")
    private boolean isWaiting = false;

    @Column(name = "est_staff")
    private boolean isStaff = false;

    @Column(name = "staff_valide")
    private boolean isStaffValidated = false;

    @Column(name = "date_inscription")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "montant_a_payer")
    private Double amountToPay;

    @Column(name = "points_utilises")
    private Integer usedPoints = 0;

    @Column(name = "montant_reduction_voucher")
    private Double voucherDiscount = 0.0;

    @Column(name = "points_gagnes")
    private Integer earnedPoints = 0;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Event event;

    public Registration() {
    }

    public Registration(User u, Event e) {
        this.user = u;
        this.event = e;
        this.hasPaid = false;
        this.hasMeal = false;
        this.isWaiting = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
    }

    public boolean isHasMeal() {
        return hasMeal;
    }

    public void setHasMeal(boolean hasMeal) {
        this.hasMeal = hasMeal;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public boolean isStaffValidated() {
        return isStaffValidated;
    }

    public void setStaffValidated(boolean staffValidated) {
        isStaffValidated = staffValidated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public Double getAmountToPay() {
        if (amountToPay == null && event != null && user != null) {
            return user.isContributor()
                    ? (event.getMemberPrice() != null ? event.getMemberPrice() : 0.0)
                    : (event.getNonMemberPrice() != null ? event.getNonMemberPrice() : 0.0);
        }
        return amountToPay;
    }

    public void setAmountToPay(Double amountToPay) {
        this.amountToPay = amountToPay;
    }

    public Integer getUsedPoints() {
        return usedPoints;
    }

    public void setUsedPoints(Integer usedPoints) {
        this.usedPoints = usedPoints;
    }

    public Double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(Double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public Integer getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(Integer earnedPoints) {
        this.earnedPoints = earnedPoints;
    }
}
