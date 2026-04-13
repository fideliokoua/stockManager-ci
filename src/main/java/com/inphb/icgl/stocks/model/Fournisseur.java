package com.inphb.icgl.stocks.model;

import javafx.beans.property.*;

public class Fournisseur {

    private IntegerProperty id;
    private StringProperty  nom;
    private StringProperty  telephone;
    private StringProperty  email;
    private StringProperty  adresse;
    private StringProperty  ville;

    public Fournisseur() {
        this.id        = new SimpleIntegerProperty(0);
        this.nom       = new SimpleStringProperty("");
        this.telephone = new SimpleStringProperty("");
        this.email     = new SimpleStringProperty("");
        this.adresse   = new SimpleStringProperty("");
        this.ville     = new SimpleStringProperty("");
    }

    public Fournisseur(int id, String nom, String telephone,
                       String email, String adresse, String ville) {
        this.id        = new SimpleIntegerProperty(id);
        this.nom       = new SimpleStringProperty(nom);
        this.telephone = new SimpleStringProperty(telephone);
        this.email     = new SimpleStringProperty(email);
        this.adresse   = new SimpleStringProperty(adresse);
        this.ville     = new SimpleStringProperty(ville);
    }

    public Fournisseur(String nom, String telephone,
                       String email, String adresse, String ville) {
        this(0, nom, telephone, email, adresse, ville);
    }

    public IntegerProperty idProperty()        { return id; }
    public StringProperty  nomProperty()       { return nom; }
    public StringProperty  telephoneProperty() { return telephone; }
    public StringProperty  emailProperty()     { return email; }
    public StringProperty  adresseProperty()   { return adresse; }
    public StringProperty  villeProperty()     { return ville; }

    public int    getId()          { return id.get(); }
    public String getNom()         { return nom.get(); }
    public String getTelephone()   { return telephone.get(); }
    public String getEmail()       { return email.get(); }
    public String getAdresse()     { return adresse.get(); }
    public String getVille()       { return ville.get(); }

    public void setId(int id)                  { this.id.set(id); }
    public void setNom(String nom)             { this.nom.set(nom); }
    public void setTelephone(String telephone) { this.telephone.set(telephone); }
    public void setEmail(String email)         { this.email.set(email); }
    public void setAdresse(String adresse)     { this.adresse.set(adresse); }
    public void setVille(String ville)         { this.ville.set(ville); }

    @Override
    public String toString() { return nom.get() + " — " + ville.get(); }
}
