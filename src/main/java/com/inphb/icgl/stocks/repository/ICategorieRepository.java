package com.inphb.icgl.stocks.repository;

import com.inphb.icgl.stocks.model.Categorie;
import javafx.collections.ObservableList;

public interface ICategorieRepository {

    boolean save(Categorie c);
    boolean update(Categorie c);
    boolean delete(int id);
    Categorie findById(int id);
    ObservableList<Categorie> findAll(int page, int size);
    int countAll();
    ObservableList<Categorie> search(String mot, int page, int size);
    int countSearch(String mot);
}
