package br.tec.dev2b.app.financeiro.service;

import br.tec.dev2b.app.financeiro.dto.MovimentoFinanceiroDto;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioMensalidadesService {

    private final MovimentoFinanceiroRepository repository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] HEADERS = {"Nome", "Profissional", "Paciente", "Data", "Parcela", "Status", "Valor Pago", "Valor"};

    @Transactional(readOnly = true)
    public List<MovimentoFinanceiroDto> listar(UUID empresaId) {
        return repository.findByEmpresaIdAndTipoOrderByDataVencimentoDesc(empresaId, "MENSALIDADE")
                .stream()
                .map(MovimentoFinanceiroDto::from)
                .toList();
    }

    private static String fmt(BigDecimal v) {
        return String.format("%.2f", v).replace(".", ",");
    }

    private static String esc(String s) {
        if (s == null) return "-";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public byte[] gerarPdf(List<MovimentoFinanceiroDto> movimentos) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            doc.add(new Paragraph("Relatorio - Mensalidades", titleFont));
            doc.add(new Paragraph("Gerado em " + LocalDate.now().format(DATE_FMT), subFont));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(HEADERS.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(6f);
            table.setWidths(new float[]{2.5f, 2.5f, 2.0f, 1.8f, 1.5f, 1.5f, 1.8f, 1.8f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            for (String h : HEADERS) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(60, 60, 60));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            BigDecimal totalPago  = BigDecimal.ZERO;
            BigDecimal totalValor = BigDecimal.ZERO;
            boolean alt = false;

            for (MovimentoFinanceiroDto m : movimentos) {
                Color bg = alt ? new Color(245, 245, 245) : Color.WHITE;
                alt = !alt;
                String parcela     = m.getTotalParcelas() != null && m.getTotalParcelas() > 1
                        ? m.getNumeroParcela() + "/" + m.getTotalParcelas() : "Parcela Unica";
                String profissional = m.getUsuarioNome()  != null ? m.getUsuarioNome()  : "Conta da Clinica";
                String paciente    = m.getPacienteNome() != null ? m.getPacienteNome() : "-";
                String data        = m.getDataVencimento() != null ? m.getDataVencimento().format(DATE_FMT) : "-";
                BigDecimal pago    = m.getValorPago()    != null ? m.getValorPago()    : BigDecimal.ZERO;
                BigDecimal valor   = m.getValorParcela() != null ? m.getValorParcela() : BigDecimal.ZERO;
                totalPago  = totalPago.add(pago);
                totalValor = totalValor.add(valor);

                String[] row = {
                        m.getTitulo() != null ? m.getTitulo() : "-",
                        profissional, paciente, data, parcela,
                        m.getStatus() != null ? m.getStatus() : "-",
                        fmt(pago), fmt(valor)
                };
                for (int i = 0; i < row.length; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(row[i], rowFont));
                    cell.setBackgroundColor(bg);
                    cell.setHorizontalAlignment(i >= 6 ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
                    cell.setPadding(4);
                    table.addCell(cell);
                }
            }

            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
            Color totBg = new Color(230, 230, 230);
            PdfPCell totalLabel = new PdfPCell(new Phrase("Totais:", boldFont));
            totalLabel.setColspan(6);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabel.setPadding(4);
            totalLabel.setBackgroundColor(totBg);
            table.addCell(totalLabel);

            PdfPCell celPago = new PdfPCell(new Phrase(fmt(totalPago), boldFont));
            celPago.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celPago.setPadding(4);
            celPago.setBackgroundColor(totBg);
            table.addCell(celPago);

            PdfPCell celValor = new PdfPCell(new Phrase(fmt(totalValor), boldFont));
            celValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celValor.setPadding(4);
            celValor.setBackgroundColor(totBg);
            table.addCell(celValor);

            doc.add(table);
            doc.close();
            return out.toByteArray();
        }
    }

    public byte[] gerarExcel(List<MovimentoFinanceiroDto> movimentos) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Mensalidades");

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font hf = wb.createFont();
            hf.setBold(true);
            hf.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hf);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle totalStyle = wb.createCellStyle();
            totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font tf = wb.createFont();
            tf.setBold(true);
            totalStyle.setFont(tf);

            CellStyle moneyStyle = wb.createCellStyle();
            DataFormat dfmt = wb.createDataFormat();
            moneyStyle.setDataFormat(dfmt.getFormat("#,##0.00"));

            CellStyle moneyTotalStyle = wb.createCellStyle();
            moneyTotalStyle.cloneStyleFrom(totalStyle);
            moneyTotalStyle.setDataFormat(dfmt.getFormat("#,##0.00"));

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            BigDecimal totalPago  = BigDecimal.ZERO;
            BigDecimal totalValor = BigDecimal.ZERO;

            for (MovimentoFinanceiroDto m : movimentos) {
                Row row = sheet.createRow(rowNum++);
                String parcela     = m.getTotalParcelas() != null && m.getTotalParcelas() > 1
                        ? m.getNumeroParcela() + "/" + m.getTotalParcelas() : "Parcela Unica";
                String profissional = m.getUsuarioNome()  != null ? m.getUsuarioNome()  : "Conta da Clinica";
                String paciente    = m.getPacienteNome() != null ? m.getPacienteNome() : "-";
                String data        = m.getDataVencimento() != null ? m.getDataVencimento().format(DATE_FMT) : "-";
                BigDecimal pago    = m.getValorPago()    != null ? m.getValorPago()    : BigDecimal.ZERO;
                BigDecimal valor   = m.getValorParcela() != null ? m.getValorParcela() : BigDecimal.ZERO;
                totalPago  = totalPago.add(pago);
                totalValor = totalValor.add(valor);

                row.createCell(0).setCellValue(m.getTitulo() != null ? m.getTitulo() : "-");
                row.createCell(1).setCellValue(profissional);
                row.createCell(2).setCellValue(paciente);
                row.createCell(3).setCellValue(data);
                row.createCell(4).setCellValue(parcela);
                row.createCell(5).setCellValue(m.getStatus() != null ? m.getStatus() : "-");
                Cell cPago  = row.createCell(6); cPago.setCellValue(pago.doubleValue());   cPago.setCellStyle(moneyStyle);
                Cell cValor = row.createCell(7); cValor.setCellValue(valor.doubleValue()); cValor.setCellStyle(moneyStyle);
            }

            Row totRow = sheet.createRow(rowNum);
            Cell lbl = totRow.createCell(5); lbl.setCellValue("Totais:"); lbl.setCellStyle(totalStyle);
            Cell tP  = totRow.createCell(6); tP.setCellValue(totalPago.doubleValue());   tP.setCellStyle(moneyTotalStyle);
            Cell tV  = totRow.createCell(7); tV.setCellValue(totalValor.doubleValue()); tV.setCellStyle(moneyTotalStyle);

            for (int i = 0; i < HEADERS.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return out.toByteArray();
        }
    }

    public String gerarCsv(List<MovimentoFinanceiroDto> movimentos) throws IOException {
        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            for (MovimentoFinanceiroDto m : movimentos) {
                String parcela = m.getTotalParcelas() != null && m.getTotalParcelas() > 1
                        ? m.getNumeroParcela() + "/" + m.getTotalParcelas() : "Parcela Unica";
                printer.printRecord(
                        m.getTitulo()    != null ? m.getTitulo()    : "-",
                        m.getUsuarioNome()  != null ? m.getUsuarioNome()  : "Conta da Clinica",
                        m.getPacienteNome() != null ? m.getPacienteNome() : "-",
                        m.getDataVencimento() != null ? m.getDataVencimento().format(DATE_FMT) : "-",
                        parcela,
                        m.getStatus()    != null ? m.getStatus()    : "-",
                        m.getValorPago()    != null ? m.getValorPago()    : BigDecimal.ZERO,
                        m.getValorParcela() != null ? m.getValorParcela() : BigDecimal.ZERO
                );
            }
        }
        return sw.toString();
    }

    public String gerarHtml(List<MovimentoFinanceiroDto> movimentos) {
        BigDecimal totalPago  = BigDecimal.ZERO;
        BigDecimal totalValor = BigDecimal.ZERO;
        StringBuilder rows = new StringBuilder();
        boolean alt = false;

        for (MovimentoFinanceiroDto m : movimentos) {
            String parcela = m.getTotalParcelas() != null && m.getTotalParcelas() > 1
                    ? m.getNumeroParcela() + "/" + m.getTotalParcelas() : "Parcela Unica";
            BigDecimal pago  = m.getValorPago()    != null ? m.getValorPago()    : BigDecimal.ZERO;
            BigDecimal valor = m.getValorParcela() != null ? m.getValorParcela() : BigDecimal.ZERO;
            totalPago  = totalPago.add(pago);
            totalValor = totalValor.add(valor);
            String bg = alt ? "#f5f5f5" : "#fff"; alt = !alt;
            rows.append("<tr style=\"background:").append(bg).append("\">")
                .append("<td>").append(esc(m.getTitulo())).append("</td>")
                .append("<td>").append(esc(m.getUsuarioNome() != null ? m.getUsuarioNome() : "Conta da Clinica")).append("</td>")
                .append("<td>").append(esc(m.getPacienteNome() != null ? m.getPacienteNome() : "-")).append("</td>")
                .append("<td>").append(m.getDataVencimento() != null ? m.getDataVencimento().format(DATE_FMT) : "-").append("</td>")
                .append("<td>").append(parcela).append("</td>")
                .append("<td>").append(esc(m.getStatus())).append("</td>")
                .append("<td style=\"text-align:right\">").append(fmt(pago)).append("</td>")
                .append("<td style=\"text-align:right\">").append(fmt(valor)).append("</td>")
                .append("</tr>");
        }

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="UTF-8"/>
                  <title>Relatorio - Mensalidades</title>
                  <style>
                    body { font-family: Arial, sans-serif; font-size: 11px; margin: 20px; }
                    h2 { margin: 0 0 2px; font-size: 14px; }
                    p  { margin: 0 0 12px; color: #555; font-size: 11px; }
                    table { width: 100%%; border-collapse: collapse; }
                    th { background: #3c3c3c; color: #fff; padding: 6px 8px; text-align: center; }
                    td { padding: 5px 8px; border-bottom: 1px solid #e0e0e0; }
                    .totals td { background: #e6e6e6; font-weight: bold; }
                    @media print { button { display: none; } }
                  </style>
                </head>
                <body>
                  <button onclick="window.print()" style="margin-bottom:12px;padding:6px 14px;cursor:pointer;">Imprimir</button>
                  <h2>Relatorio - Mensalidades</h2>
                  <p>Gerado em %s</p>
                  <table>
                    <thead>
                      <tr>%s</tr>
                    </thead>
                    <tbody>
                      %s
                    </tbody>
                    <tfoot class="totals">
                      <tr>
                        <td colspan="6" style="text-align:right">Totais:</td>
                        <td style="text-align:right">%s</td>
                        <td style="text-align:right">%s</td>
                      </tr>
                    </tfoot>
                  </table>
                </body>
                </html>
                """.formatted(
                LocalDate.now().format(DATE_FMT),
                Arrays.stream(HEADERS).map(h -> "<th>" + h + "</th>").collect(Collectors.joining()),
                rows.toString(),
                fmt(totalPago),
                fmt(totalValor)
        );
    }
}
