package br.tec.dev2b.app.dashboard.service;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import br.tec.dev2b.app.agendamento.repository.AgendamentoRepository;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.evolucao.repository.EvolucaoRepository;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.infra.minio.MinioService;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioDashboardService {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter CHART_FMT = DateTimeFormatter.ofPattern("dd/MM");

    private final AgendamentoRepository      agendamentoRepository;
    private final MovimentoFinanceiroRepository movimentoRepository;
    private final PacienteRepository          pacienteRepository;
    private final EvolucaoRepository          evolucaoRepository;
    private final EmpresaRepository           empresaRepository;
    private final MinioService                minioService;
    private final TemplateEngine              templateEngine;

    // ── View models ────────────────────────────────────────────────────────────

    public record AgendamentoVm(
            String hora,
            String paciente,
            String profissional,
            String duracao,
            String status,
            String statusCor
    ) {}

    public record KpiVm(
            String totalRecebido,
            String totalAReceber,
            String saldo,
            boolean saldoPositivo,
            long sessoesAtendidas,
            long sessoesDesmarcadas,
            long faltas,
            long evolucoesCriadas,
            long totalPacientes,
            String taxaPresenca,
            String taxaFalta,
            String taxaDesmarcamento,
            String taxaRecebimento
    ) {}

    public record DistribuicaoVm(String label, long valor, String pct) {}

    public record InsightVm(String tipo, String titulo, String texto) {}

    public record ChartFinVm(String data, String recebidos, String aReceber) {}

    // ── Public API ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public String gerarHtml(UUID empresaId, UUID usuarioId) {
        return renderHtml(empresaId, usuarioId);
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(UUID empresaId, UUID usuarioId) throws IOException {
        String html = renderHtml(empresaId, usuarioId);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(toXhtml(html));
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Erro ao gerar PDF do dashboard", e);
        }
    }

    // ── Internal ───────────────────────────────────────────────────────────────

    private String renderHtml(UUID empresaId, UUID usuarioId) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + empresaId));

        String logoUrl = null;
        if (empresa.getLogoUrl() != null && !empresa.getLogoUrl().isBlank()) {
            try {
                logoUrl = minioService.getPublicUrl(minioService.getBucketFotos(), empresa.getLogoUrl());
            } catch (Exception ignored) {}
        }

        LocalDate hoje     = LocalDate.now();
        LocalDate inicio30 = hoje.minusDays(LocalDate.now().getDayOfMonth() - 1);

        // Agendamentos do dia / próximo mês
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimMes     = hoje.plusMonths(1).atStartOfDay();
        List<Agendamento> ags = agendamentoRepository
                .findByEmpresaIdAndPeriodo(empresaId, inicioHoje, fimMes);

        if (usuarioId != null) {
            final UUID uid = usuarioId;
            ags = ags.stream().filter(a -> uid.equals(a.getUsuario() != null ? a.getUsuario().getId() : null)).toList();
        }

        String profissionalFiltro = null;
        if (usuarioId != null && !ags.isEmpty()) {
            profissionalFiltro = ags.get(0).getUsuarioNome();
        }

        List<AgendamentoVm> agendamentosVm = ags.stream().map(a -> new AgendamentoVm(
                a.getInicio() != null ? a.getInicio().format(TIME_FMT) : "-",
                a.getPacienteNome() != null ? a.getPacienteNome() : "-",
                a.getUsuarioNome() != null ? a.getUsuarioNome() : "-",
                duracaoMinutos(a.getInicio(), a.getFim()),
                capitalize(a.getStatus()),
                statusCor(a.getStatus())
        )).toList();

        // KPIs dos últimos 30 dias
        LocalDateTime inicio30Dt = inicio30.atStartOfDay();
        List<Agendamento> ags30 = agendamentoRepository
                .findByEmpresaIdAndPeriodo(empresaId, inicio30Dt, fimMes);

        long sessoesAtendidas   = ags30.stream().filter(a -> "atendido".equalsIgnoreCase(a.getStatus())).count();
        long sessoesDesmarcadas = ags30.stream().filter(a -> "desmarcado".equalsIgnoreCase(a.getStatus())).count();
        long faltas             = ags30.stream().filter(a -> "faltou".equalsIgnoreCase(a.getStatus())).count();
        long evolucoesCriadas   = evolucaoRepository.countByPacienteEmpresaIdAndDataBetween(empresaId, inicio30, hoje);

        List<MovimentoFinanceiro> movimentos = movimentoRepository.findByEmpresaIdOrderByDataVencimentoDesc(empresaId);
        BigDecimal totalRecebido = movimentos.stream()
                .filter(m -> "AGENDAMENTO".equals(m.getTipo()))
                .map(MovimentoFinanceiro::getValorPago)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAReceber = movimentos.stream()
                .filter(m -> "EM_ABERTO".equalsIgnoreCase(m.getStatus()) && m.getValorParcela() != null)
                .map(MovimentoFinanceiro::getValorParcela)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Paciente> pacientes = pacienteRepository.findByEmpresaIdOrderByNomeAsc(empresaId);
        long totalPacientes = pacientes.size();

        BigDecimal saldoVal        = totalRecebido.subtract(totalAReceber);
        BigDecimal receitaPotencial = totalRecebido.add(totalAReceber);
        long totalSessoes = sessoesAtendidas + sessoesDesmarcadas + faltas;
        String taxaPresenca      = totalSessoes > 0 ? String.format(Locale.US, "%.0f%%", sessoesAtendidas   * 100.0 / totalSessoes) : "\u2014";
        String taxaFalta         = totalSessoes > 0 ? String.format(Locale.US, "%.0f%%", faltas             * 100.0 / totalSessoes) : "\u2014";
        String taxaDesmarcamento = totalSessoes > 0 ? String.format(Locale.US, "%.0f%%", sessoesDesmarcadas * 100.0 / totalSessoes) : "\u2014";
        String taxaRecebimento   = receitaPotencial.compareTo(BigDecimal.ZERO) > 0
                ? String.format(Locale.US, "%.0f%%",
                        totalRecebido.divide(receitaPotencial, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).doubleValue())
                : "\u2014";

        KpiVm kpi = new KpiVm(
                fmtBRL(totalRecebido),
                fmtBRL(totalAReceber),
                fmtBRL(saldoVal),
                saldoVal.compareTo(BigDecimal.ZERO) >= 0,
                sessoesAtendidas, sessoesDesmarcadas, faltas,
                evolucoesCriadas, totalPacientes,
                taxaPresenca, taxaFalta, taxaDesmarcamento, taxaRecebimento
        );

        // Distribuições
        List<DistribuicaoVm> distGenero    = toDistribuicao(pacientes.stream().collect(Collectors.groupingBy(p -> nvl(p.getGenero(),     "Não informado"), Collectors.counting())));
        List<DistribuicaoVm> distGrupo     = toDistribuicao(pacientes.stream().collect(Collectors.groupingBy(p -> nvl(p.getGrupo(),      "Não informado"), Collectors.counting())));
        List<DistribuicaoVm> distStatus    = toDistribuicao(pacientes.stream().collect(Collectors.groupingBy(p -> nvl(p.getStatusPagamento(), "Ativo"),    Collectors.counting())));
        List<DistribuicaoVm> distConheceu  = toDistribuicao(pacientes.stream().collect(Collectors.groupingBy(p -> nvl(p.getComoConheceu(), "Não informado"), Collectors.counting())));

        // Chart financeiro (próximos 30 dias como tabela)
        LocalDate fimFin30 = hoje.plusDays(29);
        Map<String, BigDecimal[]> finMap = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) finMap.put(hoje.plusDays(i).format(CHART_FMT), new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        for (MovimentoFinanceiro m : movimentos) {
            LocalDate ref = m.getDataVencimento();
            if (ref == null || ref.isBefore(hoje) || ref.isAfter(fimFin30)) continue;
            String key = ref.format(CHART_FMT);
            if (!finMap.containsKey(key)) continue;
            if (m.getValorPago() != null) finMap.get(key)[0] = finMap.get(key)[0].add(m.getValorPago());
            if ("EM_ABERTO".equalsIgnoreCase(m.getStatus()) && m.getValorParcela() != null)
                finMap.get(key)[1] = finMap.get(key)[1].add(m.getValorParcela());
        }
        List<ChartFinVm> chartFin = finMap.entrySet().stream()
                .filter(e -> e.getValue()[0].compareTo(BigDecimal.ZERO) > 0 || e.getValue()[1].compareTo(BigDecimal.ZERO) > 0)
                .map(e -> new ChartFinVm(e.getKey(), fmtBRL(e.getValue()[0]), fmtBRL(e.getValue()[1])))
                .toList();

        // Build context
        Context ctx = new Context(Locale.forLanguageTag("pt-BR"));
        ctx.setVariable("empresaNome",        empresa.getNomeComercial());
        ctx.setVariable("logoUrl",            logoUrl);
        ctx.setVariable("dataGeracao",        hoje.format(DATE_FMT));
        ctx.setVariable("profissionalFiltro", profissionalFiltro);
        ctx.setVariable("agendamentos",       agendamentosVm);
        ctx.setVariable("kpi",                kpi);
        ctx.setVariable("distGenero",         distGenero);
        ctx.setVariable("distGrupo",          distGrupo);
        ctx.setVariable("distStatus",         distStatus);
        ctx.setVariable("distConheceu",       distConheceu);
        ctx.setVariable("chartFin",      chartFin);
        ctx.setVariable("insightPairs",  computeInsightPairs(kpi, receitaPotencial, totalSessoes, distConheceu, distGenero));

        return templateEngine.process("relatorio-dashboard", ctx);
    }

    // ── Insight computation ────────────────────────────────────────────────────

    private List<List<InsightVm>> computeInsightPairs(
            KpiVm kpi, BigDecimal receitaPotencial, long totalSessoes,
            List<DistribuicaoVm> distConheceu, List<DistribuicaoVm> distGenero) {

        List<InsightVm> list = new ArrayList<>();

        // Financial
        if (receitaPotencial.compareTo(BigDecimal.ZERO) > 0) {
            double pct = parseRate(kpi.taxaRecebimento());
            if (pct >= 75) {
                list.add(new InsightVm("positivo", "Sa\u00fade Financeira Positiva",
                        String.format(Locale.US,
                                "%s da receita potencial foi recebida (R$ %s de R$ %s poss\u00edveis). "
                                + "Resultado do per\u00edodo: %s.",
                                kpi.taxaRecebimento(), kpi.totalRecebido(),
                                fmtBRL(receitaPotencial),
                                kpi.saldoPositivo() ? "favor\u00e1vel" : "negativo")));
            } else if (pct >= 40) {
                list.add(new InsightVm("atencao", "Aten\u00e7\u00e3o: Inadimpl\u00eancia Moderada",
                        String.format(Locale.US,
                                "%s da receita potencial foi recebida. H\u00e1 R$ %s em aberto. "
                                + "Recomenda-se contato ativo com pacientes inadimplentes e revis\u00e3o do ciclo de cobran\u00e7as.",
                                kpi.taxaRecebimento(), kpi.totalAReceber())));
            } else {
                list.add(new InsightVm("alerta", "Alerta: Alta Inadimpl\u00eancia",
                        String.format(Locale.US,
                                "Apenas %s da receita foi recebida. H\u00e1 R$ %s em aberto. "
                                + "Revise imediatamente a pol\u00edtica de cobran\u00e7as, confirma\u00e7\u00f5es e pagamentos.",
                                kpi.taxaRecebimento(), kpi.totalAReceber())));
            }
        }

        // Sessions
        if (totalSessoes > 0) {
            double presenca = parseRate(kpi.taxaPresenca());
            if (presenca >= 85) {
                list.add(new InsightVm("positivo", "Excelente Taxa de Presen\u00e7a",
                        String.format(Locale.US,
                                "%s das %d sess\u00f5es foram realizadas com sucesso. "
                                + "Taxa de aus\u00eancia de apenas %s \u2014 resultado acima da m\u00e9dia do setor (\u226415%%).",
                                kpi.taxaPresenca(), totalSessoes, kpi.taxaFalta())));
            } else if (presenca >= 65) {
                list.add(new InsightVm("atencao", "Taxa de Presen\u00e7a Moderada",
                        String.format(Locale.US,
                                "%s das sess\u00f5es realizadas. Taxa de faltas: %s (%d aus\u00eancias). "
                                + "Considere implementar confirma\u00e7\u00f5es autom\u00e1ticas 24 h antes das consultas.",
                                kpi.taxaPresenca(), kpi.taxaFalta(), kpi.faltas())));
            } else {
                list.add(new InsightVm("alerta", "Alta Taxa de Aus\u00eancias",
                        String.format(Locale.US,
                                "%s de aus\u00eancias detectadas (%d de %d sess\u00f5es). "
                                + "Revis\u00e3o urgente da pol\u00edtica de confirma\u00e7\u00f5es e remarca\u00e7\u00f5es recomendada.",
                                kpi.taxaFalta(), kpi.faltas() + kpi.sessoesDesmarcadas(), totalSessoes)));
            }
        }

        // Patients & documentation
        if (kpi.totalPacientes() > 0) {
            String topCanal  = distConheceu.isEmpty() ? null : distConheceu.get(0).label();
            String topGenero = distGenero.isEmpty()   ? null : distGenero.get(0).label();
            double mediaEv   = kpi.sessoesAtendidas() > 0
                    ? (double) kpi.evolucoesCriadas() / kpi.sessoesAtendidas() : 0;
            String tipo = mediaEv >= 0.85 ? "positivo" : (mediaEv >= 0.5 ? "info" : "atencao");
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Base de %d pacientes cadastrados. ", kpi.totalPacientes()));
            if (topGenero != null) sb.append(String.format("Perfil predominante: %s. ", topGenero));
            if (topCanal  != null) sb.append(String.format("Principal canal de capta\u00e7\u00e3o: %s. ", topCanal));
            sb.append(String.format(Locale.US,
                    "Ader\u00eancia ao prontu\u00e1rio eletr\u00f4nico: %.0f%% das sess\u00f5es possuem evolu\u00e7\u00e3o registrada.",
                    mediaEv * 100));
            list.add(new InsightVm(tipo, "Perfil da Base e Documenta\u00e7\u00e3o Cl\u00ednica", sb.toString()));
        }

        // Group into pairs
        List<List<InsightVm>> pairs = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 2) {
            List<InsightVm> pair = new ArrayList<>();
            pair.add(list.get(i));
            if (i + 1 < list.size()) pair.add(list.get(i + 1));
            pairs.add(pair);
        }
        return pairs;
    }

    private static double parseRate(String pct) {
        if (pct == null || pct.isBlank() || pct.equals("\u2014")) return 0.0;
        try { return Double.parseDouble(pct.replace("%", "").trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private String toXhtml(String html) {
        Document doc = Jsoup.parse(html);
        doc.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .charset(java.nio.charset.StandardCharsets.UTF_8);
        return doc.html();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private static String nvl(String v, String def) {
        return (v != null && !v.isBlank()) ? v : def;
    }

    private static String fmtBRL(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v)
                .setScale(2, java.math.RoundingMode.HALF_UP)
                .toPlainString()
                .replace(".", ",")
                .replaceAll("(\\d)(?=(\\d{3})+,)", "$1.");
    }

    private static String duracaoMinutos(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) return "-";
        long mins = java.time.Duration.between(inicio, fim).toMinutes();
        if (mins < 60) return mins + " min";
        long h = mins / 60, m = mins % 60;
        return m > 0 ? h + "h " + m + "min" : h + "h";
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return "-";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private static String statusCor(String status) {
        if (status == null) return "#9CA3AF";
        return switch (status.toLowerCase()) {
            case "confirmado"  -> "#7C4DFF";
            case "atendido"    -> "#10B981";
            case "faltou"      -> "#EF4444";
            case "desmarcado"  -> "#F59E0B";
            case "bloqueio"    -> "#6B7280";
            default            -> "#9CA3AF";
        };
    }

    private static List<DistribuicaoVm> toDistribuicao(Map<String, Long> map) {
        long total = map.values().stream().mapToLong(Long::longValue).sum();
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    String pct = total > 0
                            ? String.format(Locale.US, "%.1f%%", (e.getValue() * 100.0 / total))
                            : "0%";
                    return new DistribuicaoVm(e.getKey(), e.getValue(), pct);
                })
                .toList();
    }
}
