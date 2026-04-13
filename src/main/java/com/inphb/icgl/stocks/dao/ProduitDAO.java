package com.inphb.icgl.stocks.dao;
import com.inphb.icgl.stocks.model.Produit;
import com.inphb.icgl.stocks.repository.IProduitRepository;
import com.inphb.icgl.stocks.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ProduitDAO implements IProduitRepository {

    private static final String SEL =
            "SELECT p.*, c.libelle AS nom_cat, f.nom AS nom_four " +
                    "FROM produits p " +
                    "LEFT JOIN categories   c ON p.id_categorie   = c.id " +
                    "LEFT JOIN fournisseurs f ON p.id_fournisseur = f.id ";

    @Override
    public boolean save(Produit p) {
        String sql =
                "INSERT INTO produits " +
                        "(reference, designation, id_categorie, id_fournisseur, " +
                        " prix_unitaire, quantite_stock, stock_minimum, unite) " +
                        "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getReference());
            ps.setString(2, p.getDesignation());
            ps.setInt(3,    p.getIdCategorie());
            ps.setInt(4,    p.getIdFournisseur());
            ps.setDouble(5, p.getPrixUnitaire());
            ps.setInt(6,    p.getQuantiteStock());
            ps.setInt(7,    p.getStockMinimum());
            ps.setString(8, p.getUnite());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Produit p) {
        String sql =
                "UPDATE produits SET reference=?, designation=?, id_categorie=?, " +
                        "id_fournisseur=?, prix_unitaire=?, quantite_stock=?, " +
                        "stock_minimum=?, unite=? WHERE id=?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getReference());
            ps.setString(2, p.getDesignation());
            ps.setInt(3,    p.getIdCategorie());
            ps.setInt(4,    p.getIdFournisseur());
            ps.setDouble(5, p.getPrixUnitaire());
            ps.setInt(6,    p.getQuantiteStock());
            ps.setInt(7,    p.getStockMinimum());
            ps.setString(8, p.getUnite());
            ps.setInt(9,    p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "DELETE FROM produits WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Produit findById(int id) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SEL + "WHERE p.id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Produit> findAll(int page, int size) {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     SEL + "ORDER BY p.designation LIMIT ? OFFSET ?")) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Produit> findAllSansPage() {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(SEL + "ORDER BY p.designation")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findAllSansPage] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM produits")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Produit> search(String mot, int page, int size) {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        String q = SEL +
                "WHERE p.designation LIKE ? OR c.libelle LIKE ? OR f.nom LIKE ? " +
                "ORDER BY p.designation LIMIT ? OFFSET ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(q)) {
            String m = "%" + mot + "%";
            ps.setString(1, m);
            ps.setString(2, m);
            ps.setString(3, m);
            ps.setInt(4, size);
            ps.setInt(5, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String mot) {
        String q =
                "SELECT COUNT(*) FROM produits p " +
                        "LEFT JOIN categories   c ON p.id_categorie   = c.id " +
                        "LEFT JOIN fournisseurs f ON p.id_fournisseur = f.id " +
                        "WHERE p.designation LIKE ? OR c.libelle LIKE ? OR f.nom LIKE ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(q)) {
            String m = "%" + mot + "%";
            ps.setString(1, m);
            ps.setString(2, m);
            ps.setString(3, m);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Produit> findEnAlerte() {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(
                     SEL + "WHERE p.quantite_stock <= p.stock_minimum " +
                             "ORDER BY (p.quantite_stock - p.stock_minimum) ASC LIMIT 5")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findEnAlerte] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countEnAlerte() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*) FROM produits WHERE quantite_stock <= stock_minimum")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.countEnAlerte] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double getValeurTotaleStock() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT SUM(quantite_stock * prix_unitaire) FROM produits")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.getValeurTotaleStock] " + e.getMessage());
        }
        return 0.0;
    }

    private Produit map(ResultSet rs) throws SQLException {
        return new Produit(
                rs.getInt("id"),
                rs.getString("reference"),
                rs.getString("designation"),
                rs.getInt("id_categorie"),
                rs.getInt("id_fournisseur"),
                rs.getString("nom_cat")  != null ? rs.getString("nom_cat")  : "",
                rs.getString("nom_four") != null ? rs.getString("nom_four") : "",
                rs.getDouble("prix_unitaire"),
                rs.getInt("quantite_stock"),
                rs.getInt("stock_minimum"),
                rs.getString("unite")
        );
    }
}
