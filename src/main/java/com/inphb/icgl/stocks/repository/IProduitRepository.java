package com.inphb.icgl.stocks.repository;

import com.inphb.icgl.stocks.model.Produit;
import javafx.collections.ObservableList;

public interface IProduitRepository {

    boolean save(Produit p);
    boolean update(Produit p);
    boolean delete(int id);
    Produit findById(int id);
    ObservableList<Produit> findAll(int page, int size);
    int countAll();
    ObservableList<Produit> search(String mot, int page, int size);
    int countSearch(String mot);
    ObservableList<Produit> findEnAlerte();
    int countEnAlerte();
    ObservableList<Produit> findAllSansPage();
    double getValeurTotaleStock();
}
