package com.inphb.icgl.stocks.repository;

import com.inphb.icgl.stocks.model.Utilisateur;
import javafx.collections.ObservableList;

public interface IUtilisateurRepository {

    boolean save(Utilisateur u);
    boolean update(Utilisateur u);
    boolean toggleActif(int id, boolean actif);
    Utilisateur findById(int id);
    Utilisateur findByLogin(String login);
    Utilisateur authentifier(String login, String motDePasse);
    ObservableList<Utilisateur> findAll(int page, int size);
    int countAll();
}
