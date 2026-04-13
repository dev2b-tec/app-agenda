package br.tec.dev2b.app.financeiro.service;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import br.tec.dev2b.app.agendamento.model.AgendamentoServico;
import br.tec.dev2b.app.agendamento.repository.AgendamentoRepository;
import br.tec.dev2b.app.financeiro.dto.MovimentoFinanceiroDto;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.paciente.model.Paciente;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioContasReceberService {

    private final MovimentoFinanceiroRepository repository;
    private final AgendamentoRepository agendamentoRepository;

    private static final DateTimeFormatter DATE_FMT     = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String[] HEADERS = {
        "Paciente", "CPF", "Telefone", "Data", "Profissional", "Criado Por",
        "Sala", "Servico", "Convenio", "Recorrencia", "Status", "Etiqueta",
        "Pagamento", "Data de Pagamento", "Observacao de Pagamento",
        "Metodo de Pagamento", "Valor Pago", "Valor Total"
    };

    private record ReceberRow(
        String paciente, String cpf, String telefone, String data,
        String profissional, String criadoPor, String sala, String servico,
        String convenio, String recorrencia, String statusAg, String etiqueta,
        String pagamento, String dataPagamento, String observacao,
        String metodoPagamento, BigDecimal valorPago, BigDecimal valorTotal
    ) {}

    @Transactional(readOnly = true)
    public List<MovimentoFinanceiroDto> listar(UUID empresaId) {
        return repository.findByEmpresaIdOrderByDataVencimentoDesc(empresaId)
                .stream()
                .filter(m -> !"A_PAGAR".equals(m.getTipo()))
                .map(MovimentoFinanceiroDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(UUID empresaId) throws IOException {
        List<ReceberRow> rows = buildRows(empresaId);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 20, 20, 35, 35);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK);
            Font subFont   = FontFactory.getFont(FontFactory.HELVETICA,       9, Color.DARK_GRAY);
            doc.add(new Paragraph("Relatorio - Contas a Receber", titleFont));
            doc.add(new Paragraph("Gerado em " + LocalDate.now().format(DATE_FMT), subFont));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(HEADERS.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(6f);
            table.setWidths(new float[]{3f, 2.2f, 2.2f, 2.4f, 3f, 3f, 1.8f, 2.5f, 2.2f, 1.8f, 2f, 2f, 2f, 2.4f, 3f, 2.5f, 1.8f, 2f});

            Font hf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, Color.WHITE);
            for (String h : HEADERS) {
                PdfPCell c = new PdfPCell(new Phrase(h, hf));
                c.setBackgroundColor(new Color(40, 40, 40));
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                c.setPadding(4);
                table.addCell(c);
            }

            Font rf = FontFactory.getFont(FontFactory.HELVETICA, 6.5f, Color.BLACK);
            BigDecimal totPago  = BigDecimal.ZERO;
            BigDecimal totTotal = BigDecimal.ZERO;
            boolean alt = false;

            for (ReceberRow r : rows) {
                Color bg = alt ? new Color(245, 245, 245) : Color.WHITE;
                alt = !alt;
                totPago  = totPago.add(r.valorPago());
                totTotal = totTotal.add(r.valorTotal());
                String[] cells = {
                    r.paciente(), r.cpf(), r.telefone(), r.data(),
                    r.profissional(), r.criadoPor(), r.sala(), r.servico(),
                    r.convenio(), r.recorrencia(), r.statusAg(), r.etiqueta(),
                    r.pagamento(), r.dataPagamento(), r.observacao(),
                    r.metodoPagamento(), fmt(r.valorPago()), fmt(r.valorTotal())
                };
                for (int i = 0; i < cells.length; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(cells[i], rf));
                    cell.setBackgroundColor(bg);
                    cell.setHorizontalAlignment(i >= 16 ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
                    cell.setPadding(3);
                    table.addCell(cell);
                }
            }

            Font bf    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, Color.BLACK);
            Color tBg  = new Color(220, 220, 220);
            PdfPCell lbl = new PdfPCell(new Phrase("Totais:", bf));
            lbl.setColspan(16); lbl.setHorizontalAlignment(Element.ALIGN_RIGHT);
            lbl.setPadding(4);  lbl.setBackgroundColor(tBg);
            table.addCell(lbl);
            PdfPCell cP = new PdfPCell(new Phrase(fmt(totPago), bf));
            cP.setHorizontalAlignment(Element.ALIGN_RIGHT); cP.setPadding(4); cP.setBackgroundColor(tBg);
            table.addCell(cP);
            PdfPCell cT = new PdfPCell(new Phrase(fmt(totTotal), bf));
            cT.setHorizontalAlignment(Element.ALIGN_RIGHT); cT.setPadding(4); cT.setBackgroundColor(tBg);
            table.addCell(cT);

            doc.add(table);
            doc.close();
            return out.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] gerarExcel(UUID empresaId) throws IOException {
        List<ReceberRow> rows = buildRows(empresaId);
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Contas a Receber");

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle moneyStyle = wb.createCellStyle();
            DataFormat dfmt = wb.createDataFormat();
            moneyStyle.setDataFormat(dfmt.getFormat("#,##0.00"));

            CellStyle totStyle = wb.createCellStyle();
            totStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font tFont = wb.createFont();
            tFont.setBold(true);
            totStyle.setFont(tFont);

            CellStyle moneyTotStyle = wb.createCellStyle();
            moneyTotStyle.cloneStyleFrom(totStyle);
            moneyTotStyle.setDataFormat(dfmt.getFormat("#,##0.00"));

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            BigDecimal totPago  = BigDecimal.ZERO;
            BigDecimal totTotal = BigDecimal.ZERO;

            for (ReceberRow r : rows) {
                Row row = sheet.createRow(rowNum++);
                totPago  = totPago.add(r.valorPago());
                totTotal = totTotal.add(r.valorTotal());
                row.createCell(0).setCellValue(r.paciente());
                row.createCell(1).setCellValue(r.cpf());
                row.createCell(2).setCellValue(r.telefone());
                row.createCell(3).setCellValue(r.data());
                row.createCell(4).setCellValue(r.profissional());
                row.createCell(5).setCellValue(r.criadoPor());
                row.createCell(6).setCellValue(r.sala());
                row.createCell(7).setCellValue(r.servico());
                row.createCell(8).setCellValue(r.convenio());
                row.createCell(9).setCellValue(r.recorrencia());
                row.createCell(10).setCellValue(r.statusAg());
                row.createCell(11).setCellValue(r.etiqueta());
                row.createCell(12).setCellValue(r.pagamento());
                row.createCell(13).setCellValue(r.dataPagamento());
                row.createCell(14).setCellValue(r.observacao());
                row.createCell(15).setCellValue(r.metodoPagamento());
                Cell cP = row.createCell(16); cP.setCellValue(r.valorPago().doubleValue());  cP.setCellStyle(moneyStyle);
                Cell cT = row.createCell(17); cT.setCellValue(r.valorTotal().doubleValue()); cT.setCellStyle(moneyStyle);
            }

            Row totRow = sheet.createRow(rowNum);
            Cell lbl = totRow.createCell(15); lbl.setCellValue("Totais:"); lbl.setCellStyle(totStyle);
            Cell tp = totRow.createCell(16); tp.setCellValue(totPago.doubleValue());  tp.setCellStyle(moneyTotStyle);
            Cell tt = totRow.createCell(17); tt.setCellValue(totTotal.doubleValue()); tt.setCellStyle(moneyTotStyle);

            for (int i = 0; i < HEADERS.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return out.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public String gerarCsv(UUID empresaId) throws IOException {
        List<ReceberRow> rows = buildRows(empresaId);
        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            for (ReceberRow r : rows) {
                printer.printRecord(
                    r.paciente(), r.cpf(), r.telefone(), r.data(),
                    r.profissional(), r.criadoPor(), r.sala(), r.servico(),
                    r.convenio(), r.recorrencia(), r.statusAg(), r.etiqueta(),
                    r.pagamento(), r.dataPagamento(), r.observacao(),
                    r.metodoPagamento(), r.valorPago(), r.valorTotal()
                );
            }
        }
        return sw.toString();
    }

    @Transactional(readOnly = true)
    public String gerarHtml(UUID empresaId) {
        List<ReceberRow> rows = buildRows(empresaId);
        BigDecimal totPago  = BigDecimal.ZERO;
        BigDecimal totTotal = BigDecimal.ZERO;
        StringBuilder sb = new StringBuilder();
        boolean alt = false;

        for (ReceberRow r : rows) {
            totPago  = totPago.add(r.valorPago());
            totTotal = totTotal.add(r.valorTotal());
            String bg = alt ? "#f5f5f5" : "#fff"; alt = !alt;
            sb.append("<tr style=\"background:").append(bg).append("\">")
              .append("<td>").append(esc(r.paciente())).append("</td>")
              .append("<td>").append(esc(r.cpf())).append("</td>")
              .append("<td>").append(esc(r.telefone())).append("</td>")
              .append("<td>").append(esc(r.data())).append("</td>")
              .append("<td>").append(esc(r.profissional())).append("</td>")
              .append("<td>").append(esc(r.criadoPor())).append("</td>")
              .append("<td>").append(esc(r.sala())).append("</td>")
              .append("<td>").append(esc(r.servico())).append("</td>")
              .append("<td>").append(esc(r.convenio())).append("</td>")
              .append("<td>").append(esc(r.recorrencia())).append("</td>")
              .append("<td>").append(esc(r.statusAg())).append("</td>")
              .append("<td>").append(esc(r.etiqueta())).append("</td>")
              .append("<td>").append(esc(r.pagamento())).append("</td>")
              .append("<td>").append(esc(r.dataPagamento())).append("</td>")
              .append("<td>").append(esc(r.observacao())).append("</td>")
              .append("<td>").append(esc(r.metodoPagamento())).append("</td>")
              .append("<td style=\"text-align:right\">").append(fmt(r.valorPago())).append("</td>")
              .append("<td style=\"text-align:right\">").append(fmt(r.valorTotal())).append("</td>")
              .append("</tr>");
        }

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="UTF-8"/>
                  <title>Relatorio - Contas a Receber</title>
                  <style>
                    body { font-family: Arial, sans-serif; font-size: 10px; margin: 20px; }
                    h2 { margin: 0 0 2px; font-size: 13px; }
                    p  { margin: 0 0 12px; color: #555; font-size: 10px; }
                    table { width: 100%%; border-collapse: collapse; }
                    th { background: #282828; color: #fff; padding: 5px 6px; text-align: center; font-size: 9px; }
                    td { padding: 4px 6px; border-bottom: 1px solid #e0e0e0; }
                    .totals td { background: #dcdcdc; font-weight: bold; }
                    @media print { button { display: none; } @page { size: A4 landscape; } }
                  </style>
                </head>
                <body>
                  <button onclick="window.print()" style="margin-bottom:12px;padding:6px 14px;cursor:pointer;">Imprimir</button>
                  <h2>Relatorio - Contas a Receber</h2>
                  <p>Gerado em %s</p>
                  <table>
                    <thead><tr>%s</tr></thead>
                    <tbody>%s</tbody>
                    <tfoot>
                      <tr class="totals">
                        <td colspan="16" style="text-align:right">Totais:</td>
                        <td style="text-align:right">%s</td>
                        <td style="text-align:right">%s</td>
                      </tr>
                    </tfoot>
                  </table>
                  <script>window.onload = () => window.print();</script>
                </body>
                </html>
                """.formatted(
                    LocalDate.now().format(DATE_FMT),
                    Arrays.stream(HEADERS).map(h -> "<th>" + h + "</th>").collect(Collectors.joining()),
                    sb,
                    fmt(totPago), fmt(totTotal)
                );
    }

    // -------------------------------------------------------------------------

    private List<ReceberRow> buildRows(UUID empresaId) {
        List<MovimentoFinanceiro> movimentos = repository
                .findByEmpresaIdOrderByDataVencimentoDesc(empresaId)
                .stream()
                .filter(m -> !"A_PAGAR".equals(m.getTipo()))
                .toList();

        Set<UUID> ids = movimentos.stream()
                .map(MovimentoFinanceiro::getReferenciaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, Agendamento> agMap = agendamentoRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Agendamento::getId, a -> a));

        return movimentos.stream().map(m -> {
            Agendamento ag  = m.getReferenciaId() != null ? agMap.get(m.getReferenciaId()) : null;
            Paciente    pac = m.getPaciente();

            String data      = ag != null
                    ? ag.getInicio().format(DATETIME_FMT)
                    : (m.getDataVencimento() != null ? m.getDataVencimento().format(DATE_FMT) : "-");
            String sala      = ag != null && ag.getSala() != null ? ag.getSala() : "-";
            String servico   = ag != null
                    ? ag.getServicos().stream().map(AgendamentoServico::getServicoNome).collect(Collectors.joining(", "))
                    : "-";
            String convenio  = pac != null && pac.getPlano() != null && !pac.getPlano().isBlank()
                    ? pac.getPlano() : "PARTICULAR";
            String recorr    = ag != null && Boolean.TRUE.equals(ag.getRecorrente()) ? "Sim" : "-";
            String statusAg  = ag != null ? ag.getStatus() : "-";
            String etiqueta  = ag != null ? corToEtiqueta(ag.getCor()) : "-";
            String pagamento = movStatusLabel(m.getStatus());
            String dataPag   = m.getDataPagamento() != null ? m.getDataPagamento().format(DATE_FMT) : "-";

            String movPacNome = m.getPaciente() != null ? m.getPaciente().getNome() : null;
            String movUsuNome = m.getUsuario()  != null ? m.getUsuario().getNome()  : null;

            return new ReceberRow(
                pac != null ? pac.getNome() : (movPacNome != null ? movPacNome : "-"),
                pac != null && pac.getCpf() != null ? pac.getCpf() : "-",
                pac != null && pac.getTelefone() != null ? pac.getTelefone() : "-",
                data,
                movUsuNome != null ? movUsuNome : "-",
                movUsuNome != null ? movUsuNome : "-",
                sala, servico, convenio, recorr, statusAg, etiqueta,
                pagamento, dataPag,
                m.getObservacao() != null ? m.getObservacao() : "-",
                m.getMetodoPagamento() != null ? m.getMetodoPagamento() : "-",
                m.getValorPago()    != null ? m.getValorPago()    : BigDecimal.ZERO,
                m.getValorParcela() != null ? m.getValorParcela() : BigDecimal.ZERO
            );
        }).toList();
    }

    private static String corToEtiqueta(String cor) {
        if (cor == null) return "Branco";
        return switch (cor.toLowerCase()) {
            case "green"  -> "Verde";
            case "red"    -> "Vermelho";
            case "teal"   -> "Azul-turquesa";
            case "purple" -> "Roxo";
            default       -> "Branco";
        };
    }

    private static String movStatusLabel(String status) {
        if (status == null) return "-";
        return switch (status.toUpperCase()) {
            case "PAGO"      -> "Pago";
            case "CANCELADO" -> "Cancelado";
            default          -> "Em Aberto";
        };
    }

    private String fmt(BigDecimal v) {
        return String.format("%.2f", v);
    }

    private String esc(String s) {
        if (s == null) return "-";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
