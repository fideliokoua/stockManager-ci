package com.inphb.icgl.stocks.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Categorie {

    private IntegerProperty id;
    private StringProperty  libelle;
    private StringProperty  description;

    public Categorie() {
        this.id          = new SimpleIntegerProperty(0);
        this.libelle     = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
    }

    public Categorie(int id, String libelle, String description) {
        this.id          = new SimpleIntegerProperty(id);
        this.libelle     = new SimpleStringProperty(libelle);
        this.description = new SimpleStringProperty(description);
    }

    public Categorie(String libelle, String description) {
        this(0, libelle, description);
    }

    public IntegerProperty idProperty()          { return id; }
    public StringProperty  libelleProperty()     { return libelle; }
    public StringProperty  descriptionProperty() { return description; }

    public int    getId()              { return id.get(); }
    public String getLibelle()         { return libelle.get(); }
    public String getDescription()     { return description.get(); }

    public void setId(int id)                { this.id.set(id); }
    public void setLibelle(String libelle)   { this.libelle.set(libelle); }
    public void setDescription(String desc)  { this.description.set(desc); }

}
