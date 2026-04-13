package com.inphb.icgl.stocks.controller;


import com.inphb.icgl.stocks.dao.MouvementDAO;
import com.inphb.icgl.stocks.dao.ProduitDAO;
import com.inphb.icgl.stocks.model.Produit;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardController implements Initializable{

    @FXML private Label lblTotalProduits;
    @FXML private Label lblAlertes;
    @FXML private Label lblMouvJour;
    @FXML private Label lblValeurStock;

    @FXML private TableView<Produit>           tableCritiques;
    @FXML private TableColumn<Produit,String>  colDesignation;
    @FXML private TableColumn<Produit,Integer> colQte;
    @FXML private TableColumn<Produit,Integer> colMin;

    private final ProduitDAO   prodDao = new ProduitDAO();
    private final MouvementDAO mouvDao = new MouvementDAO();

    private static final NumberFormat NF =
            NumberFormat.getNumberInstance(Locale.FRANCE);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimum"));

        // Colorer en rouge les lignes en alerte
        tableCritiques.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                setStyle(p != null && !empty && p.isEnAlerte()
                        ? "-fx-background-color: #FFE0E0;" : "");
            }
        });

        actualiser();
    }

    private void actualiser() {
        int total  = prodDao.countAll();
        int alerte = prodDao.countEnAlerte();
        int mouvJ  = mouvDao.countDuJour();
        double val = prodDao.getValeurTotaleStock();

        lblTotalProduits.setText(String.valueOf(total));
        lblAlertes.setText(String.valueOf(alerte));
        lblMouvJour.setText(String.valueOf(mouvJ));
        lblValeurStock.setText(NF.format(val) + " FCFA");

        if (alerte > 0)
            lblAlertes.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        ObservableList<Produit> critiques = prodDao.findEnAlerte();
        tableCritiques.setItems(critiques);
    }
}
