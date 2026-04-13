#!/bin/bash

echo "=========================================="
echo "   StockManager CI — Packaging macOS"
echo "=========================================="

APP_NAME="StockManager CI"
APP_VERSION="1.0.0"
MAIN_JAR="stockmanager-ci-1.0.jar"
MAIN_CLASS="com.inphb.icgl.stocks.MainApp"
TARGET_DIR="target"
DEST_DIR="target/installer"
JAVAFX_VERSION="21.0.6"

# Chemin vers les JARs JavaFX dans .m2
JAVAFX_PATH="$HOME/.m2/repository/org/openjfx"

echo "[1/4] Compilation Maven..."
mvn clean package -q

# Dossier temporaire pour les modules JavaFX
# ✅ Créé APRÈS mvn clean (qui supprime target/)
MODS_DIR="target/javafx-mods"
mkdir -p $MODS_DIR
mkdir -p $DEST_DIR

echo "[2/4] Copie des modules JavaFX..."
cp $JAVAFX_PATH/javafx-controls/$JAVAFX_VERSION/javafx-controls-$JAVAFX_VERSION-mac.jar  $MODS_DIR/
cp $JAVAFX_PATH/javafx-fxml/$JAVAFX_VERSION/javafx-fxml-$JAVAFX_VERSION-mac.jar          $MODS_DIR/
cp $JAVAFX_PATH/javafx-graphics/$JAVAFX_VERSION/javafx-graphics-$JAVAFX_VERSION-mac.jar  $MODS_DIR/
cp $JAVAFX_PATH/javafx-base/$JAVAFX_VERSION/javafx-base-$JAVAFX_VERSION-mac.jar          $MODS_DIR/

echo "[3/4] Suppression de l'ancien installeur..."
rm -rf "$DEST_DIR/$APP_NAME-$APP_VERSION.dmg"

echo "[4/4] Création du .dmg avec jpackage..."
jpackage \
  --type dmg \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --input "$TARGET_DIR" \
  --main-jar "$MAIN_JAR" \
  --main-class "$MAIN_CLASS" \
  --dest "$DEST_DIR" \
  --icon "src/main/resources/icons/stockmanager.icns" \
  --module-path "$MODS_DIR" \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,java.sql,java.naming,java.desktop,java.logging,java.xml \
  --java-options "-Dfile.encoding=UTF-8" \
  --java-options "--enable-native-access=ALL-UNNAMED" \
  --mac-package-name "StockManagerCI" \
  --mac-package-identifier "com.inphb.icgl.stocks" \
  --vendor "INPHB IC-GL" \
  --copyright "2026 INPHB IC-GL"

echo ""
echo "✅ Package créé dans : $DEST_DIR/$APP_NAME-$APP_VERSION.dmg"
echo "=========================================="