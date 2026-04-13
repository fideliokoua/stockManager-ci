-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : lun. 13 avr. 2026 à 10:40
-- Version du serveur : 10.4.28-MariaDB
-- Version de PHP : 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `stockmanager_ci`
--

-- --------------------------------------------------------

--
-- Structure de la table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `libelle` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `categories`
--

INSERT INTO `categories` (`id`, `libelle`, `description`) VALUES
(8, 'Matériaux de construction', 'Ciment, tôles, parpaings, sable'),
(9, 'Quincaillerie générale', 'Vis, boulons, clous, écrous'),
(10, 'Outillage', 'Marteaux, pinces, scies'),
(11, 'Plomberie', 'Tuyaux, raccords, robinets'),
(12, 'Électricité', 'ÉlectricitéCâbles, prises, interrupteurs'),
(13, 'Peinture et revêtement', 'Peintures, enduits, mastics'),
(14, 'Menuiserie', 'Bois, portes, fenêtres, serrures'),
(15, 'Sanitaire', 'WC, lavabos, douches'),
(16, 'Sécurité', 'Cadenas, grilles, barreaux');

-- --------------------------------------------------------

--
-- Structure de la table `fournisseurs`
--

CREATE TABLE `fournisseurs` (
  `id` int(11) NOT NULL,
  `nom` varchar(150) NOT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `ville` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `fournisseurs`
--

INSERT INTO `fournisseurs` (`id`, `nom`, `telephone`, `email`, `adresse`, `ville`) VALUES
(13, 'CIMAF CI (Ciment de l\'Afrique)', '27 22 40 00 00', 'infos@cimaf-ci.com', 'Zone Industrielle de Yopougon', 'Abidjan'),
(14, 'SAM (Société Afri-Métal)', '07 48 00 27 00', 'contacts@sam.com', 'Koumassi Zone Industrielle', 'Abidjan'),
(15, 'BERNABÉ Côte d\'Ivoire', '27 21 21 20 30', 'infos@bernabe-ci.com', 'Treichville, Boulevard de Marseille', 'Abidjan'),
(16, 'SICABLE (Société Ivoirienne de Câbles)', '27 21 21 35 02', 'contact@sicable.ci', 'Vridi , Zone Industrielle , Rue du Textile', 'Abidjan'),
(17, 'BATIPLUS Électricité', '27 21 75 86 02', 'contact@batiplus.com', 'Deux-Plateaux', 'Abidjan'),
(18, 'UPSI Côte d\'Ivoire', '27 23 00 36 78', 'contact@upsi.ci', 'Yopougon-Gesco', 'Abidjan'),
(19, 'Saint-Gobain Côte d\'Ivoire', '27 45 00 00 43', 'infos@saint-gobain-ci.com', 'Marcory Zone 4', 'Abidjan'),
(20, 'QUINKAFRICA', '07 88 28 96 88', '', '', 'Abidjan'),
(21, 'Drocolor', '27 21 21 21 00', 'contact@drocolor-ci.com', '', 'Abidjan');

-- --------------------------------------------------------

--
-- Structure de la table `mouvements`
--

CREATE TABLE `mouvements` (
  `id` int(11) NOT NULL,
  `id_produit` int(11) NOT NULL,
  `type_mouvement` enum('ENTREE','SORTIE') NOT NULL,
  `quantite` int(11) NOT NULL,
  `motif` varchar(255) DEFAULT NULL,
  `id_utilisateur` int(11) DEFAULT NULL,
  `date_mouvement` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `mouvements`
--

INSERT INTO `mouvements` (`id`, `id_produit`, `type_mouvement`, `quantite`, `motif`, `id_utilisateur`, `date_mouvement`) VALUES
(18, 25, 'SORTIE', 200, 'Vente', 1, '2026-04-08 22:38:18'),
(19, 24, 'ENTREE', 200, 'Approvisionnement - Stock Initial', 1, '2026-04-08 22:40:53'),
(20, 28, 'SORTIE', 1000, 'Vente', 1, '2026-04-08 22:42:07'),
(21, 24, 'ENTREE', 500, 'Approvisionnement Stock Initial', 1, '2026-04-08 22:43:23'),
(22, 31, 'SORTIE', 250, 'Vente', 1, '2026-04-08 22:44:49'),
(23, 29, 'ENTREE', 500, 'Approvisionnement - Stock Initial', 1, '2026-04-08 22:47:32'),
(24, 19, 'SORTIE', 100, 'Vente', 4, '2026-04-08 22:51:24'),
(25, 39, 'SORTIE', 500, 'Vente', 4, '2026-04-11 18:08:17'),
(26, 39, 'ENTREE', 200, 'Approvisionnement', 4, '2026-04-11 18:09:56');

-- --------------------------------------------------------

--
-- Structure de la table `produits`
--

CREATE TABLE `produits` (
  `id` int(11) NOT NULL,
  `reference` varchar(30) NOT NULL,
  `designation` varchar(200) NOT NULL,
  `id_categorie` int(11) DEFAULT NULL,
  `id_fournisseur` int(11) DEFAULT NULL,
  `prix_unitaire` decimal(12,2) NOT NULL DEFAULT 0.00,
  `quantite_stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimum` int(11) NOT NULL DEFAULT 5,
  `unite` varchar(30) DEFAULT 'pièce',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `produits`
--

INSERT INTO `produits` (`id`, `reference`, `designation`, `id_categorie`, `id_fournisseur`, `prix_unitaire`, `quantite_stock`, `stock_minimum`, `unite`, `created_at`) VALUES
(19, 'CM-CPI-425', 'Ciment CPI 42.5 — 50kg', 8, 13, 5000.00, 900, 100, 'Sac', '2026-04-08 20:57:32'),
(20, 'TL-OGL-3m', 'Tôle ondulée galva 3000X1500X3', 8, 15, 15000.00, 500, 50, 'Pièce', '2026-04-08 21:09:33'),
(21, 'FR-B12', 'Fer à béton 12mm', 8, 14, 45000.00, 500, 50, 'Barre', '2026-04-08 21:16:37'),
(22, 'GRVC001', 'Gravier concassé', 8, 15, 8500.00, 200, 50, 'Tonne', '2026-04-08 21:26:26'),
(23, 'SBR-0001', 'Sable de rivière', 8, 15, 7500.00, 10000, 200, 'Tonne', '2026-04-08 21:30:35'),
(24, 'VSB-200', 'Vis à bois 4x50mm', 9, 15, 10000.00, 1000, 25, 'Boite', '2026-04-08 21:36:33'),
(25, 'BM8-60', 'Boulon M8x60 + écrou', 9, 18, 300.00, 500, 50, 'Sachet', '2026-04-08 21:41:04'),
(26, 'CG-100', 'Clou galvanisé 100mm', 9, 18, 1200.00, 600, 50, 'kg', '2026-04-08 21:44:37'),
(27, 'CHAC-80', 'Charnière acier 80mm', 9, 18, 2000.00, 250, 20, 'Paire', '2026-04-08 21:47:37'),
(28, 'TY-PVC-32', 'Tuyau PVC pression 32mm', 11, 15, 6000.00, 11000, 150, 'Mètre', '2026-04-08 21:58:47'),
(29, 'CD-PVC-40', 'Coude PVC 90° 40mm', 11, 15, 600.00, 1000, 50, 'Pièce', '2026-04-08 22:02:03'),
(30, 'SP-L000', 'Siphon lavabo', 11, 15, 3000.00, 50, 15, 'Pièce', '2026-04-08 22:04:36'),
(31, 'CBE00-2.5', 'Câble électrique 2.5mm²', 12, 16, 250.00, 50, 100, 'Mètre', '2026-04-08 22:08:14'),
(32, 'DJ16A', 'Disjoncteur 16A', 12, 17, 5000.00, 40, 10, 'Pièce', '2026-04-08 22:11:08'),
(33, 'PSE-2P', 'Prise encastrée 2P+T Legrand', 12, 17, 3500.00, 200, 100, 'Pièce', '2026-04-08 22:15:19'),
(34, 'INT0001', 'Interrupteur va-et-vient', 12, 17, 500.00, 250, 50, 'Pièce', '2026-04-08 22:17:45'),
(35, 'NEON-36', 'Tube néon 36W', 12, 17, 1500.00, 90, 50, 'Pièce', '2026-04-08 22:19:53'),
(37, 'PT-EAU', 'Peinture à Eau', 13, 21, 1300.00, 100, 50, 'Kg', '2026-04-08 22:32:39'),
(39, 'PRE-00F', 'Prise Electrique de type Français', 12, 17, 1000.00, 400, 200, 'Pièce', '2026-04-11 18:01:39');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateurs`
--

CREATE TABLE `utilisateurs` (
  `id` int(11) NOT NULL,
  `nom_complet` varchar(150) NOT NULL,
  `login` varchar(50) NOT NULL,
  `mot_de_passe` varchar(64) NOT NULL,
  `role` enum('ADMIN','GESTIONNAIRE') DEFAULT 'GESTIONNAIRE',
  `actif` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `nom_complet`, `login`, `mot_de_passe`, `role`, `actif`, `created_at`) VALUES
(1, 'Administrateur Système', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 1, '2026-03-29 19:14:51'),
(2, 'Kouamé Assi Gestionnaire', 'kouame', '90933df41ce5ec368e13e971f390bdb10e23d1f45b74d9552e6cb6a2ef524eaf', 'GESTIONNAIRE', 1, '2026-03-29 19:14:51'),
(3, 'Adjoua Bamba', 'adjoua', '73c08694769f0a920cef52ab3d354e390cb8b17547ed71a971e368b569822435', 'GESTIONNAIRE', 1, '2026-03-29 19:14:51'),
(4, 'KOUAKOU Fidèle', 'fidele', 'bd7adfa51125567e4edf79f7463ab4bd5dc67fc3bb445d7bafcc3efb3ccfa01d', 'ADMIN', 1, '2026-03-30 00:34:28');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `libelle` (`libelle`);

--
-- Index pour la table `fournisseurs`
--
ALTER TABLE `fournisseurs`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `mouvements`
--
ALTER TABLE `mouvements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_produit` (`id_produit`),
  ADD KEY `id_utilisateur` (`id_utilisateur`);

--
-- Index pour la table `produits`
--
ALTER TABLE `produits`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reference` (`reference`),
  ADD KEY `id_categorie` (`id_categorie`),
  ADD KEY `id_fournisseur` (`id_fournisseur`);

--
-- Index pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT pour la table `fournisseurs`
--
ALTER TABLE `fournisseurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT pour la table `mouvements`
--
ALTER TABLE `mouvements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT pour la table `produits`
--
ALTER TABLE `produits`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `mouvements`
--
ALTER TABLE `mouvements`
  ADD CONSTRAINT `mouvements_ibfk_1` FOREIGN KEY (`id_produit`) REFERENCES `produits` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `mouvements_ibfk_2` FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateurs` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `produits`
--
ALTER TABLE `produits`
  ADD CONSTRAINT `produits_ibfk_1` FOREIGN KEY (`id_categorie`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `produits_ibfk_2` FOREIGN KEY (`id_fournisseur`) REFERENCES `fournisseurs` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
