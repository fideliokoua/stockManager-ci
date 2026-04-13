package com.inphb.icgl.stocks.controller;

import com.inphb.icgl.stocks.dao.CategorieDAO;
import com.inphb.icgl.stocks.dao.FournisseurDAO;
import com.inphb.icgl.stocks.dao.ProduitDAO;
import com.inphb.icgl.stocks.model.Categorie;
import com.inphb.icgl.stocks.model.Fournisseur;
import com.inphb.icgl.stocks.model.Produit;
import com.inphb.icgl.stocks.utils.ExportUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
public class ProduitController implements Initializable{

    @FXML private TableView<Produit>           tableProduits;
    @FXML private TableColumn<Produit,String>  colRef;
    @FXML private TableColumn<Produit,String>  colDesig;
    @FXML private TableColumn<Produit,String>  colCat;
    @FXML private TableColumn<Produit,String>  colFour;
    @FXML private TableColumn<Produit,Double>  colPrix;
    @FXML private TableColumn<Produit,Integer> colQte;
    @FXML private TableColumn<Produit,Integer> colMin;
    @FXML private TableColumn<Produit,String>  colUnite;

    @FXML private TextField            txtRef;
    @FXML private TextField            txtDesig;
    @FXML private TextField            txtPrix;
    @FXML private TextField            txtQte;
    @FXML private TextField            txtMin;
    @FXML private TextField            txtUnite;
    @FXML private ComboBox<Categorie>   cbCategorie;
    @FXML private ComboBox<Fournisseur> cbFournisseur;
    @FXML private TextField            txtRecherche;
    @FXML private Label                lblStatut;
    @FXML private Label                lblPage;
    @FXML private Button               btnPrev;
    @FXML private Button               btnNext;

    private final ProduitDAO     prodDao = new ProduitDAO();
    private final CategorieDAO   catDao  = new CategorieDAO();
    private final FournisseurDAO fourDao = new FournisseurDAO();

    private static final int PAGE_SIZE = 15;
    private int currentPage = 1;
    private int totalPages  = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colDesig.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("nomCategorie"));
        colFour.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimum"));
        colUnite.setCellValueFactory(new PropertyValueFactory<>("unite"));

        // Alerte visuelle — fond rouge si stock ≤ minimum
        tableProduits.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                setStyle(p != null && !empty && p.isEnAlerte()
                        ? "-fx-background-color: #FFE0E0;" : "");
            }
        });

        // Sélection → remplir formulaire
        tableProduits.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> { if (sel != null) remplirFormulaire(sel); });

        // Recherche en temps réel
        txtRecherche.textProperty().addListener(
                (obs, o, n) -> { currentPage = 1; chargerDonnees(); });

        // Alimenter les ComboBox

        cbCategorie.setItems(catDao.findAll(1, 1000));
        cbCategorie.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getLibelle());
            }
        });
        cbCategorie.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categorie c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getLibelle());
            }
        });

        cbFournisseur.setItems(fourDao.findAllSansPage());
        cbFournisseur.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getNom());
            }
        });
        cbFournisseur.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getNom());
            }
        });


        chargerDonnees();
    }

    private void chargerDonnees() {
        String mot = txtRecherche.getText().trim();
        int total  = mot.isEmpty() ? prodDao.countAll() : prodDao.countSearch(mot);
        totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        ObservableList<Produit> data = mot.isEmpty()
                ? prodDao.findAll(currentPage, PAGE_SIZE)
                : prodDao.search(mot, currentPage, PAGE_SIZE);

        tableProduits.setItems(data);
        lblPage.setText("Page " + currentPage + " / " + totalPages
                + " — " + total + " produits");
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
        Produit p = lireFormulaire();
        if (p == null) return;
        if (prodDao.save(p)) {
            lblStatut.setText("Produit ajouté avec succès.");
            viderFormulaire(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur : référence déjà existante ?");
        }
    }

    @FXML
    public void handleModifier() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un produit."); return; }
        Produit p = lireFormulaire();
        if (p == null) return;
        p.setId(sel.getId());
        if (prodDao.update(p)) {
            lblStatut.setText("Produit modifié.");
            viderFormulaire(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur lors de la modification.");
        }
    }

    @FXML
    public void handleSupprimer() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un produit."); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + sel.getDesignation() + " » ?",
                ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                if (prodDao.delete(sel.getId())) {
                    lblStatut.setText("Produit supprimé.");
                    viderFormulaire(); chargerDonnees();
                } else {
                    lblStatut.setText("Impossible de supprimer ce produit.");
                }
            }
        });
    }

    @FXML
    public void handleExporterXLSX() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer en Excel");
        fc.setInitialFileName("produits_stock.xlsx");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Excel (*.xlsx)", "*.xlsx"));
        File f = fc.showSaveDialog(tableProduits.getScene().getWindow());
        if (f != null) {
            try {
                ExportUtil.exporterXLSX(prodDao.findAllSansPage(), f);
                lblStatut.setText("Export Excel réussi : " + f.getAbsolutePath());
            } catch (Exception e) {
                lblStatut.setText("Erreur export Excel : " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleExporterPDF() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer en PDF");
        fc.setInitialFileName("produits_stock.pdf");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier PDF (*.pdf)", "*.pdf"));
        File f = fc.showSaveDialog(tableProduits.getScene().getWindow());
        if (f != null) {
            try {
                ExportUtil.exporterPDF(prodDao.findAllSansPage(), f);
                lblStatut.setText("Export PDF réussi : " + f.getAbsolutePath());
            } catch (Exception e) {
                lblStatut.setText("Erreur export PDF : " + e.getMessage());
            }
        }
    }

    // ── Helpers formulaire ──────────────────────────────────────

    private Produit lireFormulaire() {
        if (txtRef.getText().trim().isEmpty()
                || txtDesig.getText().trim().isEmpty()) {
            lblStatut.setText("Référence et désignation obligatoires.");
            return null;
        }
        Produit p = new Produit();
        p.setReference(txtRef.getText().trim());
        p.setDesignation(txtDesig.getText().trim());
        if (cbCategorie.getValue() != null)
            p.setIdCategorie(cbCategorie.getValue().getId());
        if (cbFournisseur.getValue() != null)
            p.setIdFournisseur(cbFournisseur.getValue().getId());
        try {
            p.setPrixUnitaire(
                    Double.parseDouble(txtPrix.getText().replace(",", ".")));
            p.setQuantiteStock(Integer.parseInt(txtQte.getText()));
            p.setStockMinimum(Integer.parseInt(txtMin.getText()));
        } catch (NumberFormatException e) {
            lblStatut.setText("Prix / Quantité / Minimum doivent être numériques.");
            return null;
        }
        p.setUnite(txtUnite.getText().trim().isEmpty()
                ? "pièce" : txtUnite.getText().trim());
        return p;
    }

    private void remplirFormulaire(Produit p) {
        txtRef.setText(p.getReference());
        txtDesig.setText(p.getDesignation());
        txtPrix.setText(String.valueOf(p.getPrixUnitaire()));
        txtQte.setText(String.valueOf(p.getQuantiteStock()));
        txtMin.setText(String.valueOf(p.getStockMinimum()));
        txtUnite.setText(p.getUnite());
        cbCategorie.getItems().stream()
                .filter(c -> c.getId() == p.getIdCategorie())
                .findFirst().ifPresent(cbCategorie::setValue);
        cbFournisseur.getItems().stream()
                .filter(f -> f.getId() == p.getIdFournisseur())
                .findFirst().ifPresent(cbFournisseur::setValue);
    }

    private void viderFormulaire() {
        txtRef.clear(); txtDesig.clear(); txtPrix.clear();
        txtQte.clear(); txtMin.clear(); txtUnite.clear();
        cbCategorie.setValue(null);
        cbFournisseur.setValue(null);
    }
}
