package com.inphb.icgl.stocks.repository;

import com.inphb.icgl.stocks.model.Mouvement;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public interface IMouvementRepository {


    boolean save(Mouvement m);
    ObservableList<Mouvement> findAll(int page, int size);
    int countAll();
    ObservableList<Mouvement> findByProduit(int idProduit, int page, int size);
    int countByProduit(int idProduit);
    int countDuJour();
    // Filtre combiné date + produit
    ObservableList<Mouvement> findFiltre(Integer idProduit, LocalDate dateDebut,
                                         LocalDate dateFin, int page, int size);
    int countFiltre(Integer idProduit, LocalDate dateDebut, LocalDate dateFin);
}

