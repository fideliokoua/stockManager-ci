package com.inphb.icgl.stocks.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.DatePicker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mouvement {

    private IntegerProperty id;
    private IntegerProperty idProduit;
    private StringProperty  nomProduit;
    private StringProperty  typeMouvement;
    private IntegerProperty quantite;
    private StringProperty  motif;
    private IntegerProperty idUtilisateur;
    private StringProperty  nomUtilisateur;
    private LocalDateTime   dateMouvement;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Mouvement() {
        this.id            = new SimpleIntegerProperty(0);
        this.idProduit     = new SimpleIntegerProperty(0);
        this.nomProduit    = new SimpleStringProperty("");
        this.typeMouvement = new SimpleStringProperty("");
        this.quantite      = new SimpleIntegerProperty(0);
        this.motif         = new SimpleStringProperty("");
        this.idUtilisateur = new SimpleIntegerProperty(0);
        this.nomUtilisateur= new SimpleStringProperty("");
        this.dateMouvement = LocalDateTime.now();
    }

    public Mouvement(int idProduit, String typeMouvement,
                     int quantite, String motif, int idUtilisateur) {
        this();
        this.idProduit.set(idProduit);
        this.typeMouvement.set(typeMouvement);
        this.quantite.set(quantite);
        this.motif.set(motif);
        this.idUtilisateur.set(idUtilisateur);
    }

    public IntegerProperty idProperty()             { return id; }
    public IntegerProperty idProduitProperty()      { return idProduit; }
    public StringProperty  nomProduitProperty()     { return nomProduit; }
    public StringProperty  typeMouvementProperty()  { return typeMouvement; }
    public IntegerProperty quantiteProperty()       { return quantite; }
    public StringProperty  motifProperty()          { return motif; }
    public IntegerProperty idUtilisateurProperty()  { return idUtilisateur; }
    public StringProperty  nomUtilisateurProperty() { return nomUtilisateur; }

    public int           getId()             { return id.get(); }
    public int           getIdProduit()      { return idProduit.get(); }
    public String        getNomProduit()     { return nomProduit.get(); }
    public String        getTypeMouvement()  { return typeMouvement.get(); }
    public int           getQuantite()       { return quantite.get(); }
    public String        getMotif()          { return motif.get(); }
    public int           getIdUtilisateur()  { return idUtilisateur.get(); }
    public String        getNomUtilisateur() { return nomUtilisateur.get(); }
    public LocalDateTime getDateMouvement()  { return dateMouvement; }

    public void setId(int id)                       { this.id.set(id); }
    public void setIdProduit(int idProduit)         { this.idProduit.set(idProduit); }
    public void setNomProduit(String nomProduit)    { this.nomProduit.set(nomProduit); }
    public void setTypeMouvement(String type)       { this.typeMouvement.set(type); }
    public void setQuantite(int quantite)           { this.quantite.set(quantite); }
    public void setMotif(String motif)              { this.motif.set(motif); }
    public void setIdUtilisateur(int id)            { this.idUtilisateur.set(id); }
    public void setNomUtilisateur(String nom)       { this.nomUtilisateur.set(nom); }
    public void setDateMouvement(LocalDateTime d)   { this.dateMouvement = d; }

    public String getDateFormatee() {
        return dateMouvement != null ? FMT.format(dateMouvement) : "";
    }

}
