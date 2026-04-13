package com.inphb.icgl.stocks.controller;

import com.inphb.icgl.stocks.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable{

    @FXML private StackPane contentArea;
    @FXML private Label      lblUtilisateur;
    @FXML private MenuItem   menuUtilisateurs; // Visible ADMIN seulement

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUtilisateur.setText(
                SessionManager.getUtilisateur().getNomComplet()
                        + " (" + SessionManager.getUtilisateur().getRole() + ")");

        // Restreindre le menu Utilisateurs aux ADMIN
        menuUtilisateurs.setVisible(SessionManager.isAdmin());

        // Afficher le Dashboard au démarrage
        chargerVue("/fxml/Dashboard.fxml");
    }

    @FXML public void ouvrirDashboard()    { chargerVue("/fxml/Dashboard.fxml"); }
    @FXML public void ouvrirCategories()   { chargerVue("/fxml/Categorie.fxml"); }
    @FXML public void ouvrirFournisseurs() { chargerVue("/fxml/Fournisseur.fxml"); }
    @FXML public void ouvrirProduits()     { chargerVue("/fxml/Produit.fxml"); }
    @FXML public void ouvrirMouvements()   { chargerVue("/fxml/Mouvement.fxml"); }

    @FXML public void ouvrirUtilisateurs() {
        if (SessionManager.isAdmin()) chargerVue("/fxml/Utilisateur.fxml");
    }

    @FXML
    public void handleDeconnexion() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Login.fxml"));
            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loader.load(), 480, 340);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm());
            javafx.stage.Stage st = new javafx.stage.Stage();
            st.setTitle("StockManager CI — Connexion");
            st.setScene(scene);
            st.setResizable(false);
            st.show();
            ((javafx.stage.Stage) contentArea.getScene().getWindow()).close();
        } catch (Exception e) {
            System.err.println("[MainLayoutController] " + e.getMessage());
        }
    }

    private void chargerVue(String fxmlPath) {
        try {
            Node vue = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(vue);
        } catch (Exception e) {
            System.err.println("[MainLayoutController.chargerVue] " + e.getMessage());
        }
    }
}
