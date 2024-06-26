package tn.vermeg.vermegapplication.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.sql.Blob;
import java.util.Date;

@Entity
@Table(name = "projets")
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "nom_utilisateur")
    private String nomUtilisateur;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "date_entree")
    private Date dateEntree;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "derniere_analyse")
    private Date derniereMaj;

    private String description;
    private String etat;

    @Lob
    @Column(name = "log_file")
    private Blob logFile;

    public Projet(){}

    public Projet(String fileName, String username, Date dateEntree, Date derniereMaj, String description, String enCours, byte[] logFileContent) {}

    public Projet(String nom, String nomUtilisateur, Date dateEntree, Date derniereMaj, String description, String etat, Blob logFile) {
        this.nom = nom;
        this.nomUtilisateur = nomUtilisateur;
        this.dateEntree = dateEntree;
        this.derniereMaj = derniereMaj;
        this.description = description;
        this.etat = etat;
        this.logFile = logFile;
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

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
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

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Blob getLogFile() {
        return logFile;
    }

    public void setLogFile(Blob logFile) {
        this.logFile = logFile;
    }
}
