package com.inphb.icgl.stocks.controller;

import com.inphb.icgl.stocks.dao.MouvementDAO;
import com.inphb.icgl.stocks.dao.ProduitDAO;
import com.inphb.icgl.stocks.model.Mouvement;
import com.inphb.icgl.stocks.model.Produit;
import com.inphb.icgl.stocks.utils.SessionManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MouvementController implements Initializable{



    @FXML private TableView<Mouvement>           tableMouvements;
    @FXML private TableColumn<Mouvement,String>  colProduit;
    @FXML private TableColumn<Mouvement,String>  colType;
    @FXML private TableColumn<Mouvement,Integer> colQte;
    @FXML private TableColumn<Mouvement,String>  colMotif;
    @FXML private TableColumn<Mouvement,String>  colDate;
    @FXML private TableColumn<Mouvement,String>  colUser;

    // Formulaire enregistrement
    @FXML private ComboBox<Produit> cbProduit;
    @FXML private ComboBox<String>  cbType;
    @FXML private TextField         txtQuantite;
    @FXML private TextField         txtMotif;
    @FXML private Label             lblStockActuel;

    // Filtres
    @FXML private ComboBox<Produit> cbFiltreProduit;
    @FXML private DatePicker        dpDateDebut;
    @FXML private DatePicker        dpDateFin;

    // Navigation & statut
    @FXML private Label  lblStatut;
    @FXML private Label  lblPage;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    private final MouvementDAO mouvDao = new MouvementDAO();
    private final ProduitDAO   prodDao = new ProduitDAO();

    private static final int PAGE_SIZE = 15;
    private int currentPage = 1;
    private int totalPages  = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colProduit.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeMouvement"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));

        // Colorer SORTIE en rouge, ENTREE en vert
        colType.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? null : v);
                setStyle(empty ? "" :
                        "SORTIE".equals(v)
                                ? "-fx-text-fill: red;  -fx-font-weight: bold;"
                                : "-fx-text-fill: green; -fx-font-weight: bold;");
            }
        });

        // ComboBox formulaire
        cbType.getItems().addAll("ENTREE", "SORTIE");
        cbType.setValue("ENTREE");

        ObservableList<Produit> produits = prodDao.findAllSansPage();

        cbProduit.setItems(produits);
        cbProduit.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDesignation());
            }
        });
        cbProduit.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDesignation());
            }
        });
        cbProduit.valueProperty().addListener((obs, o, sel) ->
                lblStockActuel.setText(sel != null
                        ? "Stock actuel : " + sel.getQuantiteStock() + " " + sel.getUnite()
                        : ""));

        // ComboBox filtre produit (avec option "Tous")
        Produit tous = new Produit();
        tous.setDesignation("— Tous les produits —");
        ObservableList<Produit> produitsAvecTous =
                javafx.collections.FXCollections.observableArrayList();
        produitsAvecTous.add(tous);
        produitsAvecTous.addAll(produits);

        cbFiltreProduit.setItems(produitsAvecTous);
        cbFiltreProduit.setValue(tous);
        cbFiltreProduit.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDesignation());
            }
        });
        cbFiltreProduit.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDesignation());
            }
        });

        chargerDonnees();
    }

    // ── Pagination ──────────────────────────────────────────────

    private void chargerDonnees() {
        Integer idProduit  = getIdProduitFiltre();
        LocalDate dateDebut = dpDateDebut.getValue();
        LocalDate dateFin   = dpDateFin.getValue();

        int total = mouvDao.countFiltre(idProduit, dateDebut, dateFin);
        totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        ObservableList<Mouvement> data =
                mouvDao.findFiltre(idProduit, dateDebut, dateFin, currentPage, PAGE_SIZE);

        tableMouvements.setItems(data);
        lblPage.setText("Page " + currentPage + " / " + totalPages
                + " — " + total + " mouvements");
        btnPrev.setDisable(currentPage <= 1);
        btnNext.setDisable(currentPage >= totalPages);
    }

    @FXML public void handlePrev() {
        if (currentPage > 1) { currentPage--; chargerDonnees(); }
    }

    @FXML public void handleNext() {
        if (currentPage < totalPages) { currentPage++; chargerDonnees(); }
    }

    // ── Filtres ─────────────────────────────────────────────────

    @FXML
    public void handleFiltrer() {
        currentPage = 1;
        chargerDonnees();
    }

    @FXML
    public void handleReinitialiserFiltres() {
        cbFiltreProduit.getSelectionModel().selectFirst(); // "Tous les produits"
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
        currentPage = 1;
        chargerDonnees();
    }

    /** Retourne l'id du produit sélectionné dans le filtre, ou null si "Tous". */
    private Integer getIdProduitFiltre() {
        Produit sel = cbFiltreProduit.getValue();
        if (sel == null || sel.getId() == 0) return null;
        return sel.getId();
    }

    // ── Enregistrement ──────────────────────────────────────────

    @FXML
    public void handleEnregistrer() {
        Produit sel = cbProduit.getValue();
        if (sel == null) { lblStatut.setText("Sélectionnez un produit."); return; }

        int qte;
        try {
            qte = Integer.parseInt(txtQuantite.getText().trim());
            if (qte <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblStatut.setText("Quantité invalide (entier > 0).");
            return;
        }

        Mouvement m = new Mouvement(
                sel.getId(),
                cbType.getValue(),
                qte,
                txtMotif.getText().trim(),
                SessionManager.getUtilisateur().getId()
        );

        if (mouvDao.save(m)) {
            lblStatut.setText("Mouvement enregistré.");
            txtQuantite.clear();
            txtMotif.clear();
            // Rafraîchir le stock affiché
            Produit maj = prodDao.findById(sel.getId());
            cbProduit.getItems().replaceAll(p -> p.getId() == maj.getId() ? maj : p);
            cbProduit.setValue(maj);
            chargerDonnees();
        } else {
            lblStatut.setText("Erreur : stock insuffisant ou produit introuvable.");
        }
    }
}
