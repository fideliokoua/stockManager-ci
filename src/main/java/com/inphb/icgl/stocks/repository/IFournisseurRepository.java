package com.inphb.icgl.stocks.repository;

import com.inphb.icgl.stocks.model.Fournisseur;
import javafx.collections.ObservableList;

public interface IFournisseurRepository {

    boolean save(Fournisseur f);
    boolean update(Fournisseur f);
    boolean delete(int id);
    Fournisseur findById(int id);
    ObservableList<Fournisseur> findAll(int page, int size);
    int countAll();
    ObservableList<Fournisseur> search(String mot, int page, int size);
    int countSearch(String mot);
    ObservableList<Fournisseur> findAllSansPage();
}
