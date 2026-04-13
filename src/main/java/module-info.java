module com.inphb.icgl.stocks {

    // ── Dépendances JavaFX ──────────────────────────────
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    // ── Base de données ─────────────────────────────────
    requires java.sql;
    requires mysql.connector.j;

    // ── Export PDF ──────────────────────────────────────
    requires itextpdf;

    // ── Export Excel ────────────────────────────────────
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    // ── Java Desktop (SplashScreen, AWT) ────────────────
    requires java.desktop;

    // ── Logging ─────────────────────────────────────────
    requires java.logging;

    // ── Ouverture des packages à JavaFX ─────────────────
    opens com.inphb.icgl.stocks            to javafx.fxml;
    opens com.inphb.icgl.stocks.controller to javafx.fxml;
    opens com.inphb.icgl.stocks.model      to javafx.fxml, javafx.base;
    opens com.inphb.icgl.stocks.utils      to javafx.fxml;

    // ── Exports ─────────────────────────────────────────
    exports com.inphb.icgl.stocks;
    exports com.inphb.icgl.stocks.controller;
    exports com.inphb.icgl.stocks.model;
    exports com.inphb.icgl.stocks.utils;
    exports com.inphb.icgl.stocks.repository;
    exports com.inphb.icgl.stocks.dao;

}