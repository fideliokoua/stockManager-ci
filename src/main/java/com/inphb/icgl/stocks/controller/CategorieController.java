package com.inphb.icgl.stocks.controller;


import com.inphb.icgl.stocks.dao.CategorieDAO;
import com.inphb.icgl.stocks.model.Categorie;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class CategorieController implements Initializable{

    @FXML private TableView<Categorie>           tableCategories;
    @FXML private TableColumn<Categorie,Integer> colId;
    @FXML private TableColumn<Categorie,String>  colLibelle;
    @FXML private TableColumn<Categorie,String>  colDescription;
    @FXML private TextField txtLibelle;
    @FXML private TextField txtDescription;
    @FXML private TextField txtRecherche;
    @FXML private Label     lblStatut;
    @FXML private Label     lblPage;
    @FXML private Button    btnPrev;
    @FXML private Button    btnNext;

    private final CategorieDAO dao = new CategorieDAO();
    private static final int PAGE_SIZE = 15;
    private int currentPage = 1;
    private int totalPages  = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Sélection → remplir le formulaire
        tableCategories.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    if (sel != null) {
                        txtLibelle.setText(sel.getLibelle());
                        txtDescription.setText(sel.getDescription());
                    }
                });

        // Recherche en temps réel
        txtRecherche.textProperty().addListener(
                (obs, o, n) -> { currentPage = 1; chargerDonnees(); });

        chargerDonnees();
    }

    private void chargerDonnees() {
        String mot = txtRecherche.getText().trim();
        int total  = mot.isEmpty() ? dao.countAll() : dao.countSearch(mot);
        totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        ObservableList<Categorie> data = mot.isEmpty()
                ? dao.findAll(currentPage, PAGE_SIZE)
                : dao.search(mot, currentPage, PAGE_SIZE);

        tableCategories.setItems(data);
        lblPage.setText("Page " + currentPage + " / " + totalPages
                + " — " + total + " catégories");
        btnPrev.setDisable(currentPage <= 1);
        btnNext.setDisable(currentPage >= totalPages);
    }

    @FXML public void handlePrev() {
        if (currentPage > 1) { currentPage--; chargerDonnees(); }
    }

    @FXML public void handleNext() {
        if (currentPage < totalPages) { currentPage++; chargerDonnees(); }
    }

    @FXML
    public void handleAjouter() {
        String lib = txtLibelle.getText().trim();
        if (lib.isEmpty()) { lblStatut.setText("Le libellé est requis."); return; }
        // Constructeur sans id — insertion en base
        Categorie c = new Categorie(lib, txtDescription.getText().trim());
        if (dao.save(c)) {
            lblStatut.setText("Catégorie ajoutée.");
            vider(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur : libellé déjà existant ?");
        }
    }

    @FXML
    public void handleModifier() {
        Categorie sel = tableCategories.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez une catégorie."); return; }
        sel.setLibelle(txtLibelle.getText().trim());
        sel.setDescription(txtDescription.getText().trim());
        if (dao.update(sel)) {
            lblStatut.setText("Catégorie modifiée."); vider(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur lors de la modification.");
        }
    }

    @FXML
    public void handleSupprimer() {
        Categorie sel = tableCategories.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez une catégorie."); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + sel.getLibelle() + " » ?",
                ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                if (dao.delete(sel.getId())) {
                    lblStatut.setText("Catégorie supprimée.");
                    vider(); chargerDonnees();
                } else {
                    lblStatut.setText(
                            "Impossible : des produits sont rattachés à cette catégorie.");
                }
            }
        });
    }

    private void vider() {
        txtLibelle.clear();
        txtDescription.clear();
    }
}
