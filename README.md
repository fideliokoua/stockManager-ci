Prérequis
Avant de commencer, assurez-vous d'avoir installé :

Outils   Version minimale    Téléchargement   
JDK      21                  https://www.oracle.com/java/technologies/downloads/IntelliJ 
IDEA     2023+               https://www.jetbrains.com/idea/download/
XAMPP    8.XAMPP             https://www.apachefriends.org/fr/index.html
Maven    3.8+                Inclus dans IntelliJ


1. Restauration de la base de données MySQL

Étape 1 — Démarrer XAMPP

Ouvrez XAMPP Control Panel
Cliquez sur Start à côté de MySQL
Attendez que le voyant passe au vert
Cliquez sur Start à côté de Apache (pour accéder à phpMyAdmin)

Étape 2 — Ouvrir phpMyAdmin

Ouvrez votre navigateur et accédez à :
http://localhost/phpmyadmin
Étape 3 — Créer et importer la base de données
Option A — Via phpMyAdmin (recommandée)

Dans phpMyAdmin, cliquez sur l'onglet SQL
Cliquez sur Importer dans le menu du haut
Cliquez sur Choisir un fichier
Naviguez vers le dossier du projet :

   stockmanager-ci/script database/stockmanager_ci.sql

Cliquez sur Importer (bouton en bas de page)
Un message "L'importation a été effectuée avec succès" doit apparaître

Étape 4 — Vérifier l'importation

Dans phpMyAdmin, vous devez voir la base stockmanager_ci avec 5 tables :
stockmanager_ci/
├── utilisateurs    
├── categories      
├── fournisseurs    
├── produits        
└── mouvements      

Identifiants de connexion par défaut
Login    Mot de passe    Rôle    
admin    admin123        ADMIN
fidele   fidele123       ADMIN
kouame   kouame2026      GESTIONNAIRE
adjoua   adjoua2026      GESTIONNAIRE

2. Ouverture du projet dans IntelliJ IDEA

Étape 1 — Cloner le dépôt GitHub

téléchargez le ZIP depuis GitHub :
Code → Download ZIP → Décompressez l'archive

Étape 2 — Ouvrir le projet dans IntelliJ IDEA

1. Lancez IntelliJ IDEA
2. Sur l'écran d'accueil, cliquez sur Open
3. Naviguez jusqu'au dossier stockmanager-ci
4. Sélectionnez le dossier (celui contenant pom.xml)
5. Cliquez sur OK
6. Si IntelliJ demande Trust this project ? → cliquez sur Trust Project

Étape 3 — Marquer le dossier resources

 Cette étape est obligatoire sinon les fichiers FXML ne seront pas trouvés.
1. Dans le panneau Project (à gauche), dépliez src/main/
2. Faites un clic droit sur le dossier resources
3. Allez dans Mark Directory as
4. Cliquez sur Resources Root (icône dossier bleu)
Le dossier resources doit maintenant apparaître en bleu.

Étape 4 — Configurer le SDK Java 21

1. Allez dans File → Project Structure 
2. Dans Project Settings → Project
3. Vérifiez que SDK est réglé sur JDK 21
4. Si non, cliquez sur Edit et pointez vers votre installation JDK 21
5. Cliquez sur OK

Étape 5 — Charger les dépendances Maven

IntelliJ charge normalement les dépendances automatiquement. Si ce n'est pas le cas :
1. Ouvrez le panneau Maven (icône m à droite)
2. Cliquez sur le bouton Reload All Maven Projects (icône de rechargement ↻)
3. Attendez que toutes les dépendances soient téléchargées 

Étape 6 — Vérifier la connexion à la base de données

Assurez-vous que les paramètres dans DatabaseConnection.java correspondent à votre configuration XAMPP :

// src/main/java/com/inphb/icgl/stocks/utils/DatabaseConnection.java
private static final String URL  = "jdbc:mysql://localhost:3306/stockmanager_ci";
private static final String USER = "root";
private static final String PWD  = "";   // Vide par défaut sur XAMPP

Étape 7 — Lancer l'application


1. Ouvrez le fichier MainApp.java :
   src/main/java/com/inphb/icgl/stocks/MainApp.java
2. Cliquez sur le bouton ▶ vert à gauche de public class MainApp
3. Ou utilisez le raccourci Ctrl+Shift+F10 (Windows) / Ctrl+Shift+R (Mac)

