package com.inphb.icgl.stocks.utils;

import com.inphb.icgl.stocks.model.Utilisateur;

public class SessionManager {

    private static Utilisateur utilisateurConnecte = null;

    // Constructeur privé — empêche l'instanciation
    private SessionManager() {}

    public static void setUtilisateur(Utilisateur u) { utilisateurConnecte = u; }

    public static Utilisateur getUtilisateur() { return utilisateurConnecte; }

    public static boolean isConnecte() { return utilisateurConnecte != null; }

    public static boolean isAdmin() {
        return utilisateurConnecte != null
                && "ADMIN".equals(utilisateurConnecte.getRole());
    }

    public static void logout() { utilisateurConnecte = null; }
}
