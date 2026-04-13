package com.inphb.icgl.stocks.controller;
import com.inphb.icgl.stocks.dao.FournisseurDAO;
import com.inphb.icgl.stocks.model.Fournisseur;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class FournisseurController implements Initializable{

    @FXML private TableView<Fournisseur>          tableFournisseurs;
    @FXML private TableColumn<Fournisseur,String> colNom;
    @FXML private TableColumn<Fournisseur,String> colTel;
    @FXML private TableColumn<Fournisseur,String> colMail;
    @FXML private TableColumn<Fournisseur,String> colVille;
    @FXML private TextField txtNom;
    @FXML private TextField txtTel;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtVille;
    @FXML private TextField txtRecherche;
    @FXML private Label     lblStatut;
    @FXML private Label     lblPage;
    @FXML private Button    btnPrev;
    @FXML private Button    btnNext;

    private final FournisseurDAO dao = new FournisseurDAO();
    private static final int PAGE_SIZE = 15;
    private int currentPage = 1;
    private int totalPages  = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colMail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));

        tableFournisseurs.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    if (sel != null) {
                        txtNom.setText(sel.getNom());
                        txtTel.setText(sel.getTelephone());
                        txtEmail.setText(sel.getEmail());
                        txtAdresse.setText(sel.getAdresse());
                        txtVille.setText(sel.getVille());
                    }
                });

        txtRecherche.textProperty().addListener(
                (obs, o, n) -> { currentPage = 1; chargerDonnees(); });

        chargerDonnees();
    }

    private void chargerDonnees() {
        String mot = txtRecherche.getText().trim();
        int total  = mot.isEmpty() ? dao.countAll() : dao.countSearch(mot);
        totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        ObservableList<Fournisseur> data = mot.isEmpty()
                ? dao.findAll(currentPage, PAGE_SIZE)
                : dao.search(mot, currentPage, PAGE_SIZE);

        tableFournisseurs.setItems(data);
        lblPage.setText("Page " + currentPage + " / " + totalPages
                + " — " + total + " fournisseurs");
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
        String nom = txtNom.getText().trim();
        if (nom.isEmpty()) { lblStatut.setText("Le nom est requis."); return; }
        // Constructeur sans id — insertion en base
        Fournisseur f = new Fournisseur(nom,
                txtTel.getText().trim(),
                txtEmail.getText().trim(),
                txtAdresse.getText().trim(),
                txtVille.getText().trim());
        if (dao.save(f)) {
            lblStatut.setText("Fournisseur ajouté.");
            vider(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur lors de l'ajout.");
        }
    }

    @FXML
    public void handleModifier() {
        Fournisseur sel = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un fournisseur."); return; }
        sel.setNom(txtNom.getText().trim());
        sel.setTelephone(txtTel.getText().trim());
        sel.setEmail(txtEmail.getText().trim());
        sel.setAdresse(txtAdresse.getText().trim());
        sel.setVille(txtVille.getText().trim());
        if (dao.update(sel)) {
            lblStatut.setText("Fournisseur modifié."); vider(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur lors de la modification.");
        }
    }

    @FXML
    public void handleSupprimer() {
        Fournisseur sel = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un fournisseur."); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + sel.getNom() + " » ?",
                ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                if (dao.delete(sel.getId())) {
                    lblStatut.setText("Fournisseur supprimé.");
                    vider(); chargerDonnees();
                } else {
                    lblStatut.setText(
                            "Impossible : des produits sont rattachés à ce fournisseur.");
                }
            }
        });
    }

    private void vider() {
        txtNom.clear(); txtTel.clear(); txtEmail.clear();
        txtAdresse.clear(); txtVille.clear();
    }
}
