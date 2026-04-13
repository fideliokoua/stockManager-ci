package com.inphb.icgl.stocks.dao;

import com.inphb.icgl.stocks.repository.IUtilisateurRepository;
import com.inphb.icgl.stocks.model.Utilisateur;
import com.inphb.icgl.stocks.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.security.MessageDigest;
import java.sql.*;

public class UtilisateurDAO implements IUtilisateurRepository {


    /** Hash SHA-256 identique à SHA2(mdp, 256) côté MySQL */
    public static String hashSHA256(String mdp) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(mdp.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur hashage SHA-256 : " + e.getMessage());
        }
    }

    @Override
    public boolean save(Utilisateur u) {
        String sql =
                "INSERT INTO utilisateurs (nom_complet, login, mot_de_passe, role, actif) " +
                        "VALUES (?,?,?,?,?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNomComplet());
            ps.setString(2, u.getLogin());
            ps.setString(3, hashSHA256(u.getMotDePasse()));
            ps.setString(4, u.getRole());
            ps.setInt(5,    u.isActif() ? 1 : 0);   // boolean → TINYINT(1)
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Utilisateur u) {
        boolean changeMdp =
                u.getMotDePasse() != null && !u.getMotDePasse().isEmpty();
        String sql = changeMdp
                ? "UPDATE utilisateurs " +
                "SET nom_complet=?, login=?, mot_de_passe=?, role=?, actif=? WHERE id=?"
                : "UPDATE utilisateurs " +
                "SET nom_complet=?, login=?, role=?, actif=? WHERE id=?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (changeMdp) {
                ps.setString(1, u.getNomComplet());
                ps.setString(2, u.getLogin());
                ps.setString(3, hashSHA256(u.getMotDePasse()));
                ps.setString(4, u.getRole());
                ps.setInt(5,    u.isActif() ? 1 : 0);
                ps.setInt(6,    u.getId());
            } else {
                ps.setString(1, u.getNomComplet());
                ps.setString(2, u.getLogin());
                ps.setString(3, u.getRole());
                ps.setInt(4,    u.isActif() ? 1 : 0);
                ps.setInt(5,    u.getId());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleActif(int id, boolean actif) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE utilisateurs SET actif=? WHERE id=?")) {
            ps.setInt(1, actif ? 1 : 0);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.toggleActif] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Utilisateur findById(int id) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM utilisateurs WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public Utilisateur findByLogin(String login) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM utilisateurs WHERE login=? AND actif=1")) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.findByLogin] " + e.getMessage());
        }
        return null;
    }

    @Override
    public Utilisateur authentifier(String login, String motDePasse) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM utilisateurs " +
                             "WHERE login=? AND mot_de_passe=? AND actif=1")) {
            ps.setString(1, login);
            ps.setString(2, hashSHA256(motDePasse));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.authentifier] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Utilisateur> findAll(int page, int size) {
        ObservableList<Utilisateur> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM utilisateurs ORDER BY nom_complet LIMIT ? OFFSET ?")) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM utilisateurs")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    private Utilisateur map(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getInt("id"),
                rs.getString("nom_complet"),
                rs.getString("login"),
                rs.getString("role"),
                rs.getInt("actif") == 1      // TINYINT(1) → boolean
        );
    }
}
