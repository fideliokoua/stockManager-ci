package com.inphb.icgl.stocks.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {


    // Paramètres de connexion
    private static final String URL  = "jdbc:mysql://localhost:3306/stockmanager_ci";
    private static final String USER = "root";
    private static final String PWD  = "";

    // Options utiles : ?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC

    // Instance unique (Singleton)
    private static Connection instance = null;

    // Constructeur privé — empêche l'instanciation directe
    private DatabaseConnection() {}

    // Méthode d'accès à la connexion
    public static Connection getConnection() throws SQLException {

        if (instance == null || instance.isClosed()) {

            // Étape 1 : Chargement explicite du driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL introuvable : " + e.getMessage());
            }

            // Étape 2 : Établir la connexion
            instance = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("[JDBC] Connexion établie à MySQL.");
        }
        return instance;
    }

    // Méthode de fermeture de la connexion
    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                    System.out.println("[JDBC] Connexion fermée.");
                }
            } catch (SQLException e) {
                System.err.println("[JDBC] Erreur lors de la fermeture : " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }
}
