package com.inphb.icgl.stocks;

import com.inphb.icgl.stocks.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 1. Charger la scène de Login
        FXMLLoader loginLoader = new FXMLLoader(
                getClass().getResource("/fxml/Login.fxml"));

        if (loginLoader.getLocation() == null) {
            throw new IllegalStateException("Login.fxml introuvable dans /fxml/");
        }

        Scene loginScene = new Scene(loginLoader.load(), 480, 340);

        // Appliquer le CSS
        var css = getClass().getResource("/css/styles.css");
        if (css != null) {
            loginScene.getStylesheets().add(css.toExternalForm());
        }

        Stage loginStage = new Stage();
        loginStage.setTitle("StockManager CI — Connexion");
        loginStage.setScene(loginScene);
        loginStage.setResizable(false);
        loginStage.centerOnScreen();

        // 2. Afficher le Splash Screen puis ouvrir le Login
        SplashScreen splash = new SplashScreen();
        splash.showAndProceed(loginStage);
    }

    @Override
    public void stop() {
        // Fermer proprement la connexion JDBC à la sortie
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}