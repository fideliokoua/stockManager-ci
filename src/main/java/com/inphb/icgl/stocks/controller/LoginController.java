package com.inphb.icgl.stocks.controller;

import com.inphb.icgl.stocks.dao.UtilisateurDAO;
import com.inphb.icgl.stocks.model.Utilisateur;
import com.inphb.icgl.stocks.utils.SessionManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
public class LoginController {

    @FXML private TextField     txtLogin;
    @FXML private PasswordField txtMotDePasse;
    @FXML private Label         lblErreur;
    @FXML private Button        btnConnexion;

    private final UtilisateurDAO dao = new UtilisateurDAO();
    private int tentatives = 0;
    private static final int MAX_TENTATIVES = 3;

    @FXML
    public void handleConnexion() {
        String login = txtLogin.getText().trim();
        String mdp   = txtMotDePasse.getText();

        if (login.isEmpty() || mdp.isEmpty()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur user = dao.authentifier(login, mdp);

        if (user != null) {
            SessionManager.setUtilisateur(user);
            tentatives = 0;
            ouvrirApplication();
        } else {
            tentatives++;
            if (tentatives >= MAX_TENTATIVES) {
                bloquerTemporairement();
            } else {
                lblErreur.setText("Identifiants incorrects. Tentative "
                        + tentatives + "/" + MAX_TENTATIVES);
            }
        }
    }

    private void bloquerTemporairement() {
        btnConnexion.setDisable(true);
        lblErreur.setText("Trop de tentatives. Patientez 30 secondes…");
        PauseTransition pause = new PauseTransition(Duration.seconds(30));
        pause.setOnFinished(e -> {
            tentatives = 0;
            btnConnexion.setDisable(false);
            lblErreur.setText("");
        });
        pause.play();
    }

    private void ouvrirApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/MainLayout.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 700);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm());

            Stage main = new Stage();
            main.setTitle("StockManager CI — "
                    + SessionManager.getUtilisateur().getNomComplet());
            main.setScene(scene);
            main.setMaximized(true);
            main.show();

            // Fermer la fenêtre de login
            ((Stage) btnConnexion.getScene().getWindow()).close();

        } catch (Exception e) {
            System.err.println("[LoginController] " + e.getMessage());
        }
    }

    @FXML
    public void handleQuitter() {
        System.exit(0);
    }
}
