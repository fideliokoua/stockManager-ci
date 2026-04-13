package com.inphb.icgl.stocks.utils;
import com.inphb.icgl.stocks.model.Produit;
import com.itextpdf.text.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
public class ExportUtil {


    // ─────────────────────────────────────────────────────────────
    // EXPORT XLSX (OBLIGATOIRE)
    // ─────────────────────────────────────────────────────────────
    public static void exporterXLSX(List<Produit> produits, File fichier)
            throws IOException {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Produits — StockManager CI");

            // Style en-tête : fond bleu foncé, texte blanc, gras
            XSSFCellStyle styleEntete = wb.createCellStyle();
            styleEntete.setFillForegroundColor(
                    new XSSFColor(new byte[]{(byte)27,(byte)58,(byte)107}, null));
            styleEntete.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleEntete.setAlignment(HorizontalAlignment.CENTER);
            styleEntete.setBorderBottom(BorderStyle.THIN);
            XSSFFont fontBlanc = wb.createFont();
            fontBlanc.setColor(IndexedColors.WHITE.getIndex());
            fontBlanc.setBold(true);
            styleEntete.setFont(fontBlanc);

            // Style alerte : fond rouge clair
            XSSFCellStyle styleAlerte = wb.createCellStyle();
            styleAlerte.setFillForegroundColor(
                    new XSSFColor(new byte[]{(byte)255,(byte)200,(byte)200}, null));
            styleAlerte.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Style normal
            XSSFCellStyle styleNormal = wb.createCellStyle();
            styleNormal.setBorderBottom(BorderStyle.THIN);
            styleNormal.setBorderRight(BorderStyle.THIN);

            // Ligne d'en-tête
            String[] entetes = {
                    "Référence", "Désignation", "Catégorie", "Fournisseur",
                    "Prix (FCFA)", "Quantité", "Stock Min", "Unité", "Alerte"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < entetes.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(entetes[i]);
                c.setCellStyle(styleEntete);
                sheet.setColumnWidth(i, 5000);
            }

            // Lignes de données
            int rowNum = 1;
            for (Produit p : produits) {
                Row row = sheet.createRow(rowNum++);
                boolean alerte = p.isEnAlerte();
                XSSFCellStyle st = alerte ? styleAlerte : styleNormal;

                String[] vals = {
                        p.getReference(),
                        p.getDesignation(),
                        p.getNomCategorie(),
                        p.getNomFournisseur(),
                        String.format("%.0f", p.getPrixUnitaire()),
                        String.valueOf(p.getQuantiteStock()),
                        String.valueOf(p.getStockMinimum()),
                        p.getUnite(),
                        alerte ? "⚠ OUI" : "NON"
                };
                for (int i = 0; i < vals.length; i++) {
                    Cell c = row.createCell(i);
                    c.setCellValue(vals[i]);
                    c.setCellStyle(st);
                }
            }

            // Figer la ligne d'en-tête
            sheet.createFreezePane(0, 1);

            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                wb.write(fos);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // EXPORT PDF (BONUS)
    // ─────────────────────────────────────────────────────────────
    public static void exporterPDF(List<Produit> produits, File fichier)
            throws Exception {

        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(fichier));
        doc.open();

        // Titre
        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph titre = new Paragraph(
                "StockManager CI — Liste des Produits", titleFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(4f);
        doc.add(titre);

        // Sous-titre date
        Font subFont = FontFactory.getFont(
                FontFactory.HELVETICA, 10, BaseColor.GRAY);
        doc.add(new Paragraph("Généré le : " + LocalDate.now(), subFont));
        doc.add(Chunk.NEWLINE);

        // Tableau
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 3.5f, 2f, 2.5f, 1.8f, 1.5f, 1.5f, 1.5f, 1.2f});

        Font hFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        BaseColor headBg = new BaseColor(27, 58, 107);
        String[] cols = {
                "Référence", "Désignation", "Catégorie", "Fournisseur",
                "Prix FCFA", "Quantité", "Min", "Unité", "Alerte"
        };
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, hFont));
            cell.setBackgroundColor(headBg);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        Font dFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        BaseColor alertBg = new BaseColor(255, 200, 200);
        for (Produit p : produits) {
            BaseColor bg = p.isEnAlerte() ? alertBg : BaseColor.WHITE;
            String[] row = {
                    p.getReference(),
                    p.getDesignation(),
                    p.getNomCategorie(),
                    p.getNomFournisseur(),
                    String.format("%.0f", p.getPrixUnitaire()),
                    String.valueOf(p.getQuantiteStock()),
                    String.valueOf(p.getStockMinimum()),
                    p.getUnite(),
                    p.isEnAlerte() ? "⚠ OUI" : "NON"
            };
            for (String val : row) {
                PdfPCell c = new PdfPCell(new Phrase(val, dFont));
                c.setBackgroundColor(bg);
                c.setPadding(4f);
                table.addCell(c);
            }
        }
        doc.add(table);
        doc.close();
    }
}
