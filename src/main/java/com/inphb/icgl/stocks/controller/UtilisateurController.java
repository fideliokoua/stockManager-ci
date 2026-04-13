package com.inphb.icgl.stocks.controller;

import com.inphb.icgl.stocks.dao.UtilisateurDAO;
import com.inphb.icgl.stocks.model.Utilisateur;
import com.inphb.icgl.stocks.utils.SessionManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class UtilisateurController implements Initializable{

    @FXML private TableView<Utilisateur>           tableUsers;
    @FXML private TableColumn<Utilisateur,Integer> colId;
    @FXML private TableColumn<Utilisateur,String>  colNom;
    @FXML private TableColumn<Utilisateur,String>  colLogin;
    @FXML private TableColumn<Utilisateur,String>  colRole;
    @FXML private TableColumn<Utilisateur,Boolean> colActif;
    @FXML private TextField        txtNom;
    @FXML private TextField        txtLogin;
    @FXML private PasswordField    txtMdp;
    @FXML private ComboBox<String> cbRole;
    @FXML private Label            lblStatut;
    @FXML private Label            lblPage;
    @FXML private Button           btnPrev;
    @FXML private Button           btnNext;

    private final UtilisateurDAO dao = new UtilisateurDAO();
    private static final int PAGE_SIZE = 15;
    private int currentPage = 1;
    private int totalPages  = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Sécurité supplémentaire : seuls les ADMIN accèdent à cette vue
        if (!SessionManager.isAdmin()) return;

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        cbRole.getItems().addAll("ADMIN", "GESTIONNAIRE");
        cbRole.setValue("GESTIONNAIRE");

        tableUsers.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    if (sel != null) {
                        txtNom.setText(sel.getNomComplet());
                        txtLogin.setText(sel.getLogin());
                        txtMdp.clear(); // ne jamais afficher le mot de passe
                        cbRole.setValue(sel.getRole());
                    }
                });

        chargerDonnees();
    }

    private void chargerDonnees() {
        int total  = dao.countAll();
        totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        ObservableList<Utilisateur> data = dao.findAll(currentPage, PAGE_SIZE);
        tableUsers.setItems(data);
        lblPage.setText("Page " + currentPage + " / " + totalPages
                + " — " + total + " utilisateurs");
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
        if (txtNom.getText().trim().isEmpty()
                || txtLogin.getText().trim().isEmpty()
                || txtMdp.getText().isEmpty()) {
            lblStatut.setText("Nom, login et mot de passe requis.");
            return;
        }
        Utilisateur u = new Utilisateur();
        u.setNomComplet(txtNom.getText().trim());
        u.setLogin(txtLogin.getText().trim());
        u.setMotDePasse(txtMdp.getText());
        u.setRole(cbRole.getValue());
        u.setActif(true);
        if (dao.save(u)) {
            lblStatut.setText("Utilisateur créé."); vider(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur : login déjà utilisé ?");
        }
    }

    @FXML
    public void handleModifier() {
        Utilisateur sel = tableUsers.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un utilisateur."); return; }
        sel.setNomComplet(txtNom.getText().trim());
        sel.setLogin(txtLogin.getText().trim());
        sel.setMotDePasse(txtMdp.getText()); // vide = pas de changement de mdp
        sel.setRole(cbRole.getValue());
        if (dao.update(sel)) {
            lblStatut.setText("Utilisateur modifié."); vider(); chargerDonnees();
        } else {
            lblStatut.setText("Erreur lors de la modification.");
        }
    }

    @FXML
    public void handleToggleActif() {
        Utilisateur sel = tableUsers.getSelectionModel().getSelectedItem();
        if (sel == null) { lblStatut.setText("Sélectionnez un utilisateur."); return; }
        // Interdire à l'admin de se désactiver lui-même
        if (sel.getId() == SessionManager.getUtilisateur().getId()) {
            lblStatut.setText("Vous ne pouvez pas vous désactiver vous-même.");
            return;
        }
        boolean nouvelEtat = !sel.isActif();
        if (dao.toggleActif(sel.getId(), nouvelEtat)) {
            lblStatut.setText(nouvelEtat ? "Compte activé." : "Compte désactivé.");
            chargerDonnees();
        }
    }

    private void vider() {
        txtNom.clear();
        txtLogin.clear();
        txtMdp.clear();
        cbRole.setValue("GESTIONNAIRE");
    }
}
