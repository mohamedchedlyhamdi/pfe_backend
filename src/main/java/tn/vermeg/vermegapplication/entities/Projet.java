package tn.vermeg.vermegapplication.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "projets")

public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private Date dateEntree;
    private Date derniereMaj;
    private String description;
private String etat;

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Projet() {
    }

    public Projet(String nom, Date dateEntree, Date derniereMaj, String description) {
        this.nom = nom;
        this.dateEntree = dateEntree;
        this.derniereMaj = derniereMaj;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(Date dateEntree) {
        this.dateEntree = dateEntree;
    }

    public Date getDerniereMaj() {
        return derniereMaj;
    }

    public void setDerniereMaj(Date derniereMaj) {
        this.derniereMaj = derniereMaj;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
