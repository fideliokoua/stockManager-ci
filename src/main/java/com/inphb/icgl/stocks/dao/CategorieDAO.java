package com.inphb.icgl.stocks.dao;

import com.inphb.icgl.stocks.model.Categorie;
import com.inphb.icgl.stocks.repository.ICategorieRepository;
import com.inphb.icgl.stocks.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CategorieDAO implements ICategorieRepository {

    @Override
    public boolean save(Categorie c) {
        String sql = "INSERT INTO categories (libelle, description) VALUES (?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getLibelle());
            ps.setString(2, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Categorie c) {
        String sql = "UPDATE categories SET libelle=?, description=? WHERE id=?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getLibelle());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        // Vérifier qu'aucun produit n'est rattaché
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM produits WHERE id_categorie=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.delete-check] " + e.getMessage());
            return false;
        }
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "DELETE FROM categories WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Categorie findById(int id) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM categories WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Categorie> findAll(int page, int size) {
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM categories ORDER BY libelle LIMIT ? OFFSET ?")) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM categories")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Categorie> search(String mot, int page, int size) {
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM categories WHERE libelle LIKE ? " +
                             "ORDER BY libelle LIMIT ? OFFSET ?")) {
            ps.setString(1, "%" + mot + "%");
            ps.setInt(2, size);
            ps.setInt(3, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String mot) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM categories WHERE libelle LIKE ?")) {
            ps.setString(1, "%" + mot + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    private Categorie map(ResultSet rs) throws SQLException {
        return new Categorie(
                rs.getInt("id"),
                rs.getString("libelle"),
                rs.getString("description")
        );
    }
}
