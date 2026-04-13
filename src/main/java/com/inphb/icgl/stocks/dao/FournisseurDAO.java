package com.inphb.icgl.stocks.dao;
import com.inphb.icgl.stocks.model.Fournisseur;
import com.inphb.icgl.stocks.repository.IFournisseurRepository;
import com.inphb.icgl.stocks.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
public class FournisseurDAO implements IFournisseurRepository{

    private static final String BASE_SELECT = "SELECT * FROM fournisseurs";

    @Override
    public boolean save(Fournisseur f) {
        String sql = "INSERT INTO fournisseurs (nom, telephone, email, adresse, ville) " +
                "VALUES (?,?,?,?,?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, f.getNom());
            ps.setString(2, f.getTelephone());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getAdresse());
            ps.setString(5, f.getVille());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Fournisseur f) {
        String sql = "UPDATE fournisseurs " +
                "SET nom=?, telephone=?, email=?, adresse=?, ville=? WHERE id=?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, f.getNom());
            ps.setString(2, f.getTelephone());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getAdresse());
            ps.setString(5, f.getVille());
            ps.setInt(6, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        // Vérifier qu'aucun produit n'est rattaché
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM produits WHERE id_fournisseur=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.delete-check] " + e.getMessage());
            return false;
        }
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "DELETE FROM fournisseurs WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Fournisseur findById(int id) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     BASE_SELECT + " WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Fournisseur> findAll(int page, int size) {
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     BASE_SELECT + " ORDER BY nom LIMIT ? OFFSET ?")) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Fournisseur> findAllSansPage() {
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(BASE_SELECT + " ORDER BY nom")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.findAllSansPage] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM fournisseurs")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Fournisseur> search(String mot, int page, int size) {
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     BASE_SELECT + " WHERE nom LIKE ? OR telephone LIKE ? " +
                             "ORDER BY nom LIMIT ? OFFSET ?")) {
            String p = "%" + mot + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setInt(3, size);
            ps.setInt(4, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String mot) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM fournisseurs WHERE nom LIKE ? OR telephone LIKE ?")) {
            String p = "%" + mot + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    private Fournisseur map(ResultSet rs) throws SQLException {
        return new Fournisseur(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("adresse"),
                rs.getString("ville")
        );
    }
}
