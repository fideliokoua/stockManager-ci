package com.inphb.icgl.stocks.model;

import javafx.beans.property.*;
import javafx.scene.input.DataFormat;

public class Produit {

    private IntegerProperty id;
    private StringProperty  reference;
    private StringProperty  designation;
    private IntegerProperty idCategorie;
    private IntegerProperty idFournisseur;
    private StringProperty  nomCategorie;
    private StringProperty  nomFournisseur;
    private DoubleProperty  prixUnitaire;
    private IntegerProperty quantiteStock;
    private IntegerProperty stockMinimum;
    private StringProperty  unite;

    public Produit() {
        this.id            = new SimpleIntegerProperty(0);
        this.reference     = new SimpleStringProperty("");
        this.designation   = new SimpleStringProperty("");
        this.idCategorie   = new SimpleIntegerProperty(0);
        this.idFournisseur = new SimpleIntegerProperty(0);
        this.nomCategorie  = new SimpleStringProperty("");
        this.nomFournisseur= new SimpleStringProperty("");
        this.prixUnitaire  = new SimpleDoubleProperty(0.0);
        this.quantiteStock = new SimpleIntegerProperty(0);
        this.stockMinimum  = new SimpleIntegerProperty(5);
        this.unite         = new SimpleStringProperty("pièce");
    }

    public Produit(int id, String reference, String designation,
                   int idCategorie, int idFournisseur,
                   String nomCategorie, String nomFournisseur,
                   double prixUnitaire, int quantiteStock,
                   int stockMinimum, String unite) {
        this.id            = new SimpleIntegerProperty(id);
        this.reference     = new SimpleStringProperty(reference);
        this.designation   = new SimpleStringProperty(designation);
        this.idCategorie   = new SimpleIntegerProperty(idCategorie);
        this.idFournisseur = new SimpleIntegerProperty(idFournisseur);
        this.nomCategorie  = new SimpleStringProperty(nomCategorie);
        this.nomFournisseur= new SimpleStringProperty(nomFournisseur);
        this.prixUnitaire  = new SimpleDoubleProperty(prixUnitaire);
        this.quantiteStock = new SimpleIntegerProperty(quantiteStock);
        this.stockMinimum  = new SimpleIntegerProperty(stockMinimum);
        this.unite         = new SimpleStringProperty(unite);
    }

    public IntegerProperty idProperty()            { return id; }
    public StringProperty  referenceProperty()     { return reference; }
    public StringProperty  designationProperty()   { return designation; }
    public IntegerProperty idCategorieProperty()   { return idCategorie; }
    public IntegerProperty idFournisseurProperty() { return idFournisseur; }
    public StringProperty  nomCategorieProperty()  { return nomCategorie; }
    public StringProperty  nomFournisseurProperty(){ return nomFournisseur; }
    public DoubleProperty  prixUnitaireProperty()  { return prixUnitaire; }
    public IntegerProperty quantiteStockProperty() { return quantiteStock; }
    public IntegerProperty stockMinimumProperty()  { return stockMinimum; }
    public StringProperty  uniteProperty()         { return unite; }

    public int    getId()             { return id.get(); }
    public String getReference()      { return reference.get(); }
    public String getDesignation()    { return designation.get(); }
    public int    getIdCategorie()    { return idCategorie.get(); }
    public int    getIdFournisseur()  { return idFournisseur.get(); }
    public String getNomCategorie()   { return nomCategorie.get(); }
    public String getNomFournisseur() { return nomFournisseur.get(); }
    public double getPrixUnitaire()   { return prixUnitaire.get(); }
    public int    getQuantiteStock()  { return quantiteStock.get(); }
    public int    getStockMinimum()   { return stockMinimum.get(); }
    public String getUnite()          { return unite.get(); }

    public void setId(int id)                      { this.id.set(id); }
    public void setReference(String reference)     { this.reference.set(reference); }
    public void setDesignation(String designation) { this.designation.set(designation); }
    public void setIdCategorie(int idCategorie)    { this.idCategorie.set(idCategorie); }
    public void setIdFournisseur(int idFournisseur){ this.idFournisseur.set(idFournisseur); }
    public void setNomCategorie(String v)          { this.nomCategorie.set(v); }
    public void setNomFournisseur(String v)        { this.nomFournisseur.set(v); }
    public void setPrixUnitaire(double prixUnitaire){ this.prixUnitaire.set(prixUnitaire); }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock.set(quantiteStock); }
    public void setStockMinimum(int stockMinimum)   { this.stockMinimum.set(stockMinimum); }
    public void setUnite(String unite)              { this.unite.set(unite); }

    public boolean isEnAlerte() {
        return quantiteStock.get() <= stockMinimum.get();
    }

    @Override
    public String toString() {
        return designation.get() + " [" + reference.get() + "]";
    }
}


