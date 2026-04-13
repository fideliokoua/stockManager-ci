package com.inphb.icgl.stocks.model;

import javafx.beans.property.*;

public class Utilisateur {

    private IntegerProperty id;
    private StringProperty  nomComplet;
    private StringProperty  login;
    private StringProperty  motDePasse;
    private StringProperty  role;
    private BooleanProperty actif;

    public Utilisateur() {
        this.id         = new SimpleIntegerProperty(0);
        this.nomComplet = new SimpleStringProperty("");
        this.login      = new SimpleStringProperty("");
        this.motDePasse = new SimpleStringProperty("");
        this.role       = new SimpleStringProperty("GESTIONNAIRE");
        this.actif      = new SimpleBooleanProperty(true);
    }

    public Utilisateur(int id, String nomComplet, String login,
                       String role, boolean actif) {
        this.id         = new SimpleIntegerProperty(id);
        this.nomComplet = new SimpleStringProperty(nomComplet);
        this.login      = new SimpleStringProperty(login);
        this.motDePasse = new SimpleStringProperty("");
        this.role       = new SimpleStringProperty(role);
        this.actif      = new SimpleBooleanProperty(actif);
    }

    public IntegerProperty idProperty()        { return id; }
    public StringProperty  nomCompletProperty(){ return nomComplet; }
    public StringProperty  loginProperty()     { return login; }
    public StringProperty  motDePasseProperty(){ return motDePasse; }
    public StringProperty  roleProperty()      { return role; }
    public BooleanProperty actifProperty()     { return actif; }

    public int     getId()          { return id.get(); }
    public String  getNomComplet()  { return nomComplet.get(); }
    public String  getLogin()       { return login.get(); }
    public String  getMotDePasse()  { return motDePasse.get(); }
    public String  getRole()        { return role.get(); }
    public boolean isActif()        { return actif.get(); }

    public void setId(int id)                  { this.id.set(id); }
    public void setNomComplet(String v)        { this.nomComplet.set(v); }
    public void setLogin(String v)             { this.login.set(v); }
    public void setMotDePasse(String v)        { this.motDePasse.set(v); }
    public void setRole(String v)              { this.role.set(v); }
    public void setActif(boolean v)            { this.actif.set(v); }

    @Override
    public String toString() {
        return nomComplet.get() + " (" + login.get() + ")";
    }
}
