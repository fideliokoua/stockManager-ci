package com.inphb.icgl.stocks.dao;

import com.inphb.icgl.stocks.model.Produit;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProduitDAOTest {

    private static ProduitDAO dao;
    private static Connection connection;
    private static Produit produitTest;
    private static int idInsere = -1;

    // URL de connexion directe — indépendante du Singleton
    private static final String URL  = "jdbc:mysql://localhost:3306/stockmanager_ci";
    private static final String USER = "root";
    private static final String PWD  = "";  // ← votre mot de passe XAMPP

    @BeforeAll
    static void initAll() throws SQLException {
        // Connexion directe indépendante du Singleton
        connection = DriverManager.getConnection(URL, USER, PWD);
        connection.setAutoCommit(false);

        dao = new ProduitDAO();

        // Récupérer un id_categorie et id_fournisseur existants
        int idCat  = 0;
        int idFour = 0;
        try (Statement st = connection.createStatement()) {
            ResultSet rs1 = st.executeQuery("SELECT id FROM categories LIMIT 1");
            if (rs1.next()) idCat = rs1.getInt(1);

            ResultSet rs2 = st.executeQuery("SELECT id FROM fournisseurs LIMIT 1");
            if (rs2.next()) idFour = rs2.getInt(1);
        }

        assertTrue(idCat  > 0, "Aucune catégorie trouvée en base !");
        assertTrue(idFour > 0, "Aucun fournisseur trouvé en base !");

        produitTest = new Produit(
                0, "TEST-REF-001", "Produit de test JUnit",
                idCat, idFour, "", "", 999.99, 50, 10, "pièce"
        );

        System.out.println("[TEST] id_categorie=" + idCat + " id_fournisseur=" + idFour);
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.setAutoCommit(true);
            connection.close();
            System.out.println("[TEST] Rollback effectué — base inchangée.");
        }
    }

    // ══════════════════════════════════════════════════════════
    // TEST 1 — save()
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("✅ save() — doit insérer un produit")
    void testSave() throws SQLException {
        String sql =
                "INSERT INTO produits (reference, designation, id_categorie, " +
                        "id_fournisseur, prix_unitaire, quantite_stock, stock_minimum, unite) " +
                        "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produitTest.getReference());
            ps.setString(2, produitTest.getDesignation());
            ps.setInt   (3, produitTest.getIdCategorie());
            ps.setInt   (4, produitTest.getIdFournisseur());
            ps.setDouble(5, produitTest.getPrixUnitaire());
            ps.setInt   (6, produitTest.getQuantiteStock());
            ps.setInt   (7, produitTest.getStockMinimum());
            ps.setString(8, produitTest.getUnite());

            int rows = ps.executeUpdate();
            assertTrue(rows > 0, "save() doit insérer au moins une ligne");

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) idInsere = keys.getInt(1);
        }

        assertTrue(idInsere > 0, "L'ID inséré doit être positif");
        System.out.println("[TEST save()] Produit inséré avec id=" + idInsere);
    }

    // ══════════════════════════════════════════════════════════
    // TEST 2 — findAll()
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("✅ findAll() — doit retourner une liste non vide contenant le produit inséré")
    void testFindAll() throws SQLException {
        String sql = "SELECT reference FROM produits WHERE reference = ?";
        boolean trouve = false;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "TEST-REF-001");
            ResultSet rs = ps.executeQuery();
            trouve = rs.next();
        }

        assertTrue(trouve, "Le produit inséré doit apparaître dans la base");

        // Vérification via DAO (liste paginée)
        ObservableList<Produit> produits = dao.findAll(1, 100);
        assertNotNull(produits, "findAll() ne doit pas retourner null");
        assertFalse(produits.isEmpty(), "findAll() doit retourner au moins un produit");

        System.out.println("[TEST findAll()] " + produits.size() + " produit(s) trouvé(s)");
    }

    // ══════════════════════════════════════════════════════════
    // TEST 3 — delete()
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("✅ delete() — doit supprimer un produit existant")
    void testDelete() throws SQLException {
        assertTrue(idInsere > 0, "L'ID doit être valide avant de supprimer");

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM produits WHERE id=?")) {
            ps.setInt(1, idInsere);
            int rows = ps.executeUpdate();
            assertTrue(rows > 0, "delete() doit supprimer au moins une ligne");
        }

        // Vérifier que le produit n'existe plus
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT id FROM produits WHERE id=?")) {
            ps.setInt(1, idInsere);
            ResultSet rs = ps.executeQuery();
            assertFalse(rs.next(), "Le produit supprimé ne doit plus exister");
        }

        System.out.println("[TEST delete()] Produit id=" + idInsere + " supprimé.");
    }

    // ══════════════════════════════════════════════════════════
    // TEST BONUS — delete() ID inexistant
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("❌ delete() — doit retourner false pour un ID inexistant")
    void testDeleteIdInexistant() {
        boolean resultat = dao.delete(999999);
        assertFalse(resultat, "delete() doit retourner false pour un ID inexistant");
        System.out.println("[TEST delete() inexistant] Retourne bien false");
    }
}