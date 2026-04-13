package com.inphb.icgl.stocks;

import javafx.stage.Stage;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen extends Stage {

    public SplashScreen() throws Exception {
        // Fenêtre sans barre de titre
        initStyle(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SplashScreen.fxml"));
        Scene scene = new Scene(loader.load(), 520, 380);
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm());

        setScene(scene);
        centerOnScreen();
    }

    /**
     * Affiche le splash pendant 3 secondes,
     * puis le ferme et ouvre la fenêtre de connexion.
     */
    public void showAndProceed(Stage loginStage) {
        show();
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            close();
            loginStage.show();
        });
        pause.play();
    }
}
