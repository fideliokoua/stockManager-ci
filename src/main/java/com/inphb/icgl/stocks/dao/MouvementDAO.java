package com.inphb.icgl.stocks.dao;
import com.inphb.icgl.stocks.model.Mouvement;
import com.inphb.icgl.stocks.repository.IMouvementRepository;
import com.inphb.icgl.stocks.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;


public class MouvementDAO implements IMouvementRepository {


    private static final String SEL =
            "SELECT m.*, p.designation AS nom_prod, u.nom_complet AS nom_user " +
                    "FROM mouvements m " +
                    "JOIN produits p ON m.id_produit = p.id " +
                    "LEFT JOIN utilisateurs u ON m.id_utilisateur = u.id ";

    @Override
    public boolean save(Mouvement m) {
        Connection cn = null;
        try {
            cn = DatabaseConnection.getConnection();
            cn.setAutoCommit(false);

            // 1. Vérifier le stock disponible pour les SORTIES
            if ("SORTIE".equals(m.getTypeMouvement())) {
                PreparedStatement chk = cn.prepareStatement(
                        "SELECT quantite_stock FROM produits WHERE id=?");
                chk.setInt(1, m.getIdProduit());
                ResultSet rs = chk.executeQuery();
                if (rs.next() && rs.getInt(1) < m.getQuantite()) {
                    cn.rollback();
                    return false; // Stock insuffisant
                }
                chk.close();
            }

            // 2. Insérer le mouvement
            PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO mouvements " +
                            "(id_produit, type_mouvement, quantite, motif, id_utilisateur) " +
                            "VALUES (?,?,?,?,?)");
            ps.setInt(1,    m.getIdProduit());
            ps.setString(2, m.getTypeMouvement());
            ps.setInt(3,    m.getQuantite());
            ps.setString(4, m.getMotif());
            ps.setInt(5,    m.getIdUtilisateur());
            ps.executeUpdate();
            ps.close();

            // 3. Mettre à jour la quantité du produit
            String op = "ENTREE".equals(m.getTypeMouvement()) ? "+" : "-";
            PreparedStatement upd = cn.prepareStatement(
                    "UPDATE produits SET quantite_stock = quantite_stock " + op + " ? WHERE id=?");
            upd.setInt(1, m.getQuantite());
            upd.setInt(2, m.getIdProduit());
            upd.executeUpdate();
            upd.close();

            cn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("[MouvementDAO.save] " + e.getMessage());
            if (cn != null) try { cn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            if (cn != null) try { cn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    @Override
    public ObservableList<Mouvement> findAll(int page, int size) {
        ObservableList<Mouvement> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     SEL + "ORDER BY m.date_mouvement DESC LIMIT ? OFFSET ?")) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM mouvements")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Mouvement> findByProduit(int idProduit, int page, int size) {
        ObservableList<Mouvement> list = FXCollections.observableArrayList();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     SEL + "WHERE m.id_produit=? " +
                             "ORDER BY m.date_mouvement DESC LIMIT ? OFFSET ?")) {
            ps.setInt(1, idProduit);
            ps.setInt(2, size);
            ps.setInt(3, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.findByProduit] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countByProduit(int idProduit) {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM mouvements WHERE id_produit=?")) {
            ps.setInt(1, idProduit);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countByProduit] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int countDuJour() {
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*) FROM mouvements WHERE DATE(date_mouvement) = CURDATE()")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countDuJour] " + e.getMessage());
        }
        return 0;
    }

    /**
     * Filtre combiné : produit et/ou plage de dates.
     * Chaque paramètre est optionnel (null = pas de filtre sur ce critère).
     */
    @Override
    public ObservableList<Mouvement> findFiltre(Integer idProduit,
                                                LocalDate dateDebut, LocalDate dateFin, int page, int size) {
        ObservableList<Mouvement> list = FXCollections.observableArrayList();
        String sql = construireRequete(false, idProduit, dateDebut, dateFin)
                + " ORDER BY m.date_mouvement DESC LIMIT ? OFFSET ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            int idx = remplirParams(ps, 1, idProduit, dateDebut, dateFin);
            ps.setInt(idx,     size);
            ps.setInt(idx + 1, (page - 1) * size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.findFiltre] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countFiltre(Integer idProduit, LocalDate dateDebut, LocalDate dateFin) {
        String sql = construireRequete(true, idProduit, dateDebut, dateFin);
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            remplirParams(ps, 1, idProduit, dateDebut, dateFin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countFiltre] " + e.getMessage());
        }
        return 0;
    }

    /** Construit dynamiquement la requête selon les filtres actifs. */
    private String construireRequete(boolean count, Integer idProduit,
                                     LocalDate dateDebut, LocalDate dateFin) {
        String select = count
                ? "SELECT COUNT(*) FROM mouvements m "
                + "JOIN produits p ON m.id_produit = p.id "
                + "LEFT JOIN utilisateurs u ON m.id_utilisateur = u.id"
                : SEL;
        StringBuilder sb = new StringBuilder(select);
        boolean hasWhere = false;
        if (idProduit != null) {
            sb.append(" WHERE m.id_produit = ?");
            hasWhere = true;
        }
        if (dateDebut != null) {
            sb.append(hasWhere ? " AND" : " WHERE");
            sb.append(" DATE(m.date_mouvement) >= ?");
            hasWhere = true;
        }
        if (dateFin != null) {
            sb.append(hasWhere ? " AND" : " WHERE");
            sb.append(" DATE(m.date_mouvement) <= ?");
        }
        return sb.toString();
    }

    /** Remplit les paramètres du PreparedStatement et retourne le prochain index. */
    private int remplirParams(PreparedStatement ps, int idx,
                              Integer idProduit, LocalDate dateDebut,
                              LocalDate dateFin) throws SQLException {
        if (idProduit != null)  ps.setInt(idx++, idProduit);
        if (dateDebut != null)  ps.setDate(idx++, java.sql.Date.valueOf(dateDebut));
        if (dateFin   != null)  ps.setDate(idx++, java.sql.Date.valueOf(dateFin));
        return idx;
    }

    private Mouvement map(ResultSet rs) throws SQLException {
        Mouvement m = new Mouvement(
                rs.getInt("id_produit"),
                rs.getString("type_mouvement"),
                rs.getInt("quantite"),
                rs.getString("motif"),
                rs.getInt("id_utilisateur")
        );
        m.setId(rs.getInt("id"));
        m.setNomProduit(rs.getString("nom_prod"));
        m.setNomUtilisateur(
                rs.getString("nom_user") != null ? rs.getString("nom_user") : "");
        Timestamp ts = rs.getTimestamp("date_mouvement");
        if (ts != null) m.setDateMouvement(ts.toLocalDateTime());
        return m;
    }
}
