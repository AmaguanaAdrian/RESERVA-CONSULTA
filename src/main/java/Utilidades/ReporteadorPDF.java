package utilidades;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import modelo.LibroCompleto;

public class ReporteadorPDF {

    public static void generarReporteCatalogo(List<LibroCompleto> libros) {
        if (libros == null || libros.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "No hay datos para exportar.", 
                "Catálogo vacío", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Configurar el JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar catálogo de libros");
        fileChooser.setSelectedFile(new File("Catalogo_Libros_" + 
            java.time.LocalDate.now() + ".pdf"));
        
        // Filtrar solo archivos PDF
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }
            
            @Override
            public String getDescription() {
                return "Archivos PDF (*.pdf)";
            }
        });

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File dest = fileChooser.getSelectedFile();
            
            // Asegurar extensión .pdf
            if (!dest.getName().toLowerCase().endsWith(".pdf")) {
                dest = new File(dest.getAbsolutePath() + ".pdf");
            }
            
            // Verificar si el archivo ya existe
            if (dest.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(null,
                    "El archivo ya existe. ¿Desea sobrescribirlo?",
                    "Archivo existente",
                    JOptionPane.YES_NO_OPTION);
                
                if (overwrite != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Document document = new Document(PageSize.A4.rotate()); // Horizontal para mejor visualización
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
                
                // Agregar metadatos
                document.addTitle("Catálogo de Libros");
                document.addSubject("Reporte del catálogo completo");
                document.addKeywords("biblioteca, libros, catálogo, pdf");
                document.addAuthor("Sistema de Biblioteca");
                document.addCreator("Sistema de Gestión de Biblioteca");
                
                document.open();

                // Título del reporte
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
                Paragraph title = new Paragraph("CATÁLOGO DE LIBROS - BIBLIOTECA", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Información de generación
                Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
                Paragraph info = new Paragraph(
                    "Generado el: " + java.time.LocalDate.now() + 
                    " | Total de libros: " + libros.size(), 
                    infoFont);
                info.setAlignment(Element.ALIGN_CENTER);
                info.setSpacingAfter(15);
                document.add(info);

                // Crear tabla con 5 columnas
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);
                
                // Configurar anchos de columnas
                float[] columnWidths = {1f, 3f, 2f, 2f, 1f};
                table.setWidths(columnWidths);
                
                // Encabezados de tabla
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                
                PdfPCell cell1 = new PdfPCell(new Phrase("ID", headerFont));
                cell1.setBackgroundColor(new BaseColor(70, 130, 180)); // Azul acero
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(5);
                
                PdfPCell cell2 = new PdfPCell(new Phrase("TÍTULO", headerFont));
                cell2.setBackgroundColor(new BaseColor(70, 130, 180));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setPadding(5);
                
                PdfPCell cell3 = new PdfPCell(new Phrase("AUTOR", headerFont));
                cell3.setBackgroundColor(new BaseColor(70, 130, 180));
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell3.setPadding(5);
                
                PdfPCell cell4 = new PdfPCell(new Phrase("GÉNERO", headerFont));
                cell4.setBackgroundColor(new BaseColor(70, 130, 180));
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell4.setPadding(5);
                
                PdfPCell cell5 = new PdfPCell(new Phrase("STOCK", headerFont));
                cell5.setBackgroundColor(new BaseColor(70, 130, 180));
                cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell5.setPadding(5);
                
                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);
                
                // Agregar filas de datos
                Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                int rowCount = 0;
                
                for (LibroCompleto libro : libros) {
                    // Alternar colores de fondo
                    BaseColor rowColor = (rowCount % 2 == 0) ? 
                        new BaseColor(240, 240, 240) : BaseColor.WHITE;
                    
                    // Celda ID
                    PdfPCell idCell = new PdfPCell(new Phrase(
                        String.valueOf(libro.getIdLibro()), dataFont));
                    idCell.setBackgroundColor(rowColor);
                    idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    idCell.setPadding(5);
                    
                    // Celda Título
                    PdfPCell titleCell = new PdfPCell(new Phrase(libro.getTitulo(), dataFont));
                    titleCell.setBackgroundColor(rowColor);
                    titleCell.setPadding(5);
                    
                    // Celda Autor
                    PdfPCell authorCell = new PdfPCell(new Phrase(libro.getNombreAutor(), dataFont));
                    authorCell.setBackgroundColor(rowColor);
                    authorCell.setPadding(5);
                    
                    // Celda Género
                    PdfPCell genreCell = new PdfPCell(new Phrase(libro.getNombreGenero(), dataFont));
                    genreCell.setBackgroundColor(rowColor);
                    genreCell.setPadding(5);
                    
                    // Celda Stock (con color según disponibilidad)
                    Phrase stockPhrase;
                    if (libro.getCantidadDisponible() > 0) {
                        stockPhrase = new Phrase(
                            String.valueOf(libro.getCantidadDisponible()), 
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.GREEN));
                    } else {
                        stockPhrase = new Phrase(
                            String.valueOf(libro.getCantidadDisponible()), 
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.RED));
                    }
                    
                    PdfPCell stockCell = new PdfPCell(stockPhrase);
                    stockCell.setBackgroundColor(rowColor);
                    stockCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    stockCell.setPadding(5);
                    
                    table.addCell(idCell);
                    table.addCell(titleCell);
                    table.addCell(authorCell);
                    table.addCell(genreCell);
                    table.addCell(stockCell);
                    
                    rowCount++;
                }
                
                document.add(table);
                
                // Pie de página
                Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
                Paragraph footer = new Paragraph(
                    "Sistema de Gestión de Biblioteca - © " + 
                    java.time.Year.now().getValue(), 
                    footerFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                footer.setSpacingBefore(20);
                document.add(footer);
                
                document.close();
                
                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(null,
                    "<html><div style='text-align: center;'>" +
                    "<h3 style='color: #28a745;'>✓ PDF generado exitosamente</h3>" +
                    "<p><b>Archivo:</b> " + dest.getName() + "</p>" +
                    "<p><b>Ubicación:</b> " + dest.getParent() + "</p>" +
                    "<p><b>Libros incluidos:</b> " + libros.size() + "</p>" +
                    "<p><small>El archivo se ha guardado correctamente.</small></p>" +
                    "</div></html>",
                    "Reporte completado",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "❌ Error al generar PDF:\n" + e.getMessage(),
                    "Error de generación",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}