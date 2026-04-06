package br.tec.dev2b.app.dashboard.service;

import br.tec.dev2b.app.agendamento.dto.AgendamentoDto;
import br.tec.dev2b.app.agendamento.model.Agendamento;
import br.tec.dev2b.app.agendamento.repository.AgendamentoRepository;
import br.tec.dev2b.app.dashboard.dto.DashboardDto;
import br.tec.dev2b.app.evolucao.repository.EvolucaoRepository;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AgendamentoRepository agendamentoRepository;
    private final MovimentoFinanceiroRepository movimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final EvolucaoRepository evolucaoRepository;

    private static final DateTimeFormatter CHART_FMT     = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter CHART_FMT_FULL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(UUID empresaId) {
        LocalDate hoje      = LocalDate.now();
        LocalDate inicio30  = hoje.minusDays(LocalDate.now().getDayOfMonth() - 1);

        // ── Agendamentos do dia ────────────────────────────────
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimMes    = hoje.plusMonths(1).atStartOfDay();
        List<AgendamentoDto> agendamentosHoje = agendamentoRepository
                .findByEmpresaIdAndPeriodo(empresaId, inicioHoje, fimMes)
                .stream()
                .map(AgendamentoDto::from)
                .toList();

        // ── Agendamentos últimos 30 dias (charts + KPIs sessões) ──
        LocalDateTime inicio30Dt = inicio30.atStartOfDay();
        LocalDateTime fim30Dt    = hoje.plusMonths(1).atStartOfDay();
        List<Agendamento> ags30 = agendamentoRepository
                .findByEmpresaIdAndPeriodo(empresaId, inicio30Dt, fim30Dt);

        long sessoesAtendidas   = ags30.stream().filter(a -> "atendido".equalsIgnoreCase(a.getStatus())).count();
        long sessoesDesmarcadas = ags30.stream().filter(a -> "desmarcado".equalsIgnoreCase(a.getStatus())).count();
        long faltas             = ags30.stream().filter(a -> "faltou".equalsIgnoreCase(a.getStatus())).count();

        // Chart sessões por dia
        Map<String, long[]> sessoesMap = new LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            sessoesMap.put(hoje.minusDays(i).format(CHART_FMT), new long[]{0, 0, 0});
        }
        for (Agendamento a : ags30) {
            String key = a.getInicio().toLocalDate().format(CHART_FMT);
            long[] arr = sessoesMap.getOrDefault(key, null);
            if (arr == null) continue;
            String st = a.getStatus() == null ? "" : a.getStatus().toLowerCase();
            if (st.equals("atendido"))   arr[0]++;
            else if (st.equals("desmarcado")) arr[1]++;
            else if (st.equals("faltou")) arr[2]++;
        }
        List<DashboardDto.ChartSessoesPoint> chartSessoes = sessoesMap.entrySet().stream()
                .map(e -> DashboardDto.ChartSessoesPoint.builder()
                        .date(e.getKey())
                        .atendidos(e.getValue()[0])
                        .desmarcados(e.getValue()[1])
                        .faltantes(e.getValue()[2])
                        .build())
                .toList();

        // Chart agendamentos por dia (total)
        Map<String, Long> agsCountMap = new LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate d = hoje.minusDays(i);
            agsCountMap.put(d.format(CHART_FMT_FULL), 0L);
        }
        for (Agendamento a : ags30) {
            String key = a.getInicio().toLocalDate().format(CHART_FMT_FULL);
            agsCountMap.merge(key, 1L, Long::sum);
        }
        List<DashboardDto.ChartAgendamentosPoint> chartAgendamentos = agsCountMap.entrySet().stream()
                .map(e -> DashboardDto.ChartAgendamentosPoint.builder()
                        .date(e.getKey())
                        .agendamentos(e.getValue())
                        .build())
                .toList();

        // ── Movimentos financeiros ─────────────────────────────
        List<MovimentoFinanceiro> movimentos = movimentoRepository.findByEmpresaIdOrderByDataVencimentoDesc(empresaId);

        BigDecimal totalRecebido  = movimentos.stream()
                .filter(m -> m.getTipo().equals("AGENDAMENTO"))
                .map(MovimentoFinanceiro::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAReceber  = movimentos.stream()
                .filter(m -> "EM_ABERTO".equalsIgnoreCase(m.getStatus()) && m.getValorParcela() != null)
                .map(MovimentoFinanceiro::getValorParcela)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Chart financeiro por dia (próximos 30 dias, baseado em dataVencimento)
        LocalDate fimFin30 = hoje.plusDays(29);
        Map<String, BigDecimal[]> finMap = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) {
            finMap.put(hoje.plusDays(i).format(CHART_FMT), new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        }
        for (MovimentoFinanceiro m : movimentos) {
            LocalDate ref = m.getDataVencimento();
            if (ref == null || ref.isBefore(hoje) || ref.isAfter(fimFin30)) continue;
            String key = ref.format(CHART_FMT);
            BigDecimal[] arr = finMap.getOrDefault(key, null);
            if (arr == null) continue;
            if (m.getValorPago() != null) {
                arr[0] = arr[0].add(m.getValorPago());
            }
            if ("EM_ABERTO".equalsIgnoreCase(m.getStatus()) && m.getValorParcela() != null) {
                arr[1] = arr[1].add(m.getValorParcela());
            }
        }
        List<DashboardDto.ChartFinanceiroPoint> chartFinanceiro = finMap.entrySet().stream()
                .map(e -> DashboardDto.ChartFinanceiroPoint.builder()
                        .date(e.getKey())
                        .recebidos(e.getValue()[0])
                        .aReceber(e.getValue()[1])
                        .build())
                .toList();

        // ── Evoluções criadas (últimos 30 dias) ──────────────────
        long evolucoesCriadas = evolucaoRepository
                .countByPacienteEmpresaIdAndDataBetween(empresaId, inicio30, hoje);

        // ── Pacientes ──────────────────────────────────────────
        List<Paciente> pacientes = pacienteRepository.findByEmpresaIdOrderByNomeAsc(empresaId);

        Map<String, Long> pacientesGenero = pacientes.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getGenero() != null && !p.getGenero().isBlank() ? p.getGenero() : "Não informado",
                        Collectors.counting()));

        Map<String, Long> pacientesGrupo = pacientes.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getGrupo() != null && !p.getGrupo().isBlank() ? p.getGrupo() : "Não informado",
                        Collectors.counting()));

        Map<String, Long> pacientesStatus = pacientes.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatusPagamento() != null && !p.getStatusPagamento().isBlank() ? p.getStatusPagamento() : "Ativo",
                        Collectors.counting()));

        Map<String, Long> pacientesComoConheceu = pacientes.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getComoConheceu() != null && !p.getComoConheceu().isBlank() ? p.getComoConheceu() : "Não informado",
                        Collectors.counting()));

        return DashboardDto.builder()
                .agendamentosHoje(agendamentosHoje)
                .totalRecebido(totalRecebido)
                .totalAReceber(totalAReceber)
                .evolucoesCriadas(evolucoesCriadas)
                .sessoesAtendidas(sessoesAtendidas)
                .sessoesDesmarcadas(sessoesDesmarcadas)
                .faltas(faltas)
                .chartFinanceiro(chartFinanceiro)
                .chartSessoes(chartSessoes)
                .chartAgendamentos(chartAgendamentos)
                .pacientesGenero(pacientesGenero)
                .pacientesGrupo(pacientesGrupo)
                .pacientesStatus(pacientesStatus)
                .pacientesComoConheceu(pacientesComoConheceu)
                .build();
    }
}
