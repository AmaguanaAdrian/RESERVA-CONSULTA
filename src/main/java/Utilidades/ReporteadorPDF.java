package Utilidades;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import modelo.LibroCompleto;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ReporteadorPDF {

    public static void generarReporteCatalogo(List<LibroCompleto> libros) {
        if (libros == null || libros.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay datos para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Catalogo_Libros.pdf"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File dest = fileChooser.getSelectedFile();
            
            // iText 5 usa un flujo de trabajo diferente al 7
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(dest));
                document.open();

                document.add(new Paragraph("BIBLIOTECA - REPORTE DE CATALOGO"));
                document.add(new Paragraph(" ")); // Espacio en blanco

                // Tabla de 4 columnas
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                
                table.addCell("Titulo");
                table.addCell("Autor");
                table.addCell("Genero");
                table.addCell("Stock");

                for (LibroCompleto libro : libros) {
                    table.addCell(libro.getTitulo());
                    table.addCell(libro.getNombreAutor());
                    table.addCell(libro.getNombreGenero());
                    table.addCell(String.valueOf(libro.getCantidadDisponible()));
                }

                document.add(table);
                document.close();
                
                JOptionPane.showMessageDialog(null, "PDF guardado en: " + dest.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
            }
        }
    }
}