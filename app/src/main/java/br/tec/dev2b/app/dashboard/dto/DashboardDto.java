package br.tec.dev2b.app.dashboard.dto;

import br.tec.dev2b.app.agendamento.dto.AgendamentoDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardDto {

    // Atividades do dia (agendamentos de hoje)
    private List<AgendamentoDto> agendamentosHoje;

    // KPIs financeiros (últimos 30 dias)
    private BigDecimal totalRecebido;
    private BigDecimal totalAReceber;

    // KPIs de sessões (últimos 30 dias)
    private long evolucoesCriadas;
    private long sessoesAtendidas;
    private long sessoesDesmarcadas;
    private long faltas;

    // Charts (últimos 30 dias, por dia)
    private List<ChartFinanceiroPoint> chartFinanceiro;
    private List<ChartSessoesPoint>    chartSessoes;
    private List<ChartAgendamentosPoint> chartAgendamentos;

    // Distribuições de pacientes
    private Map<String, Long> pacientesGenero;
    private Map<String, Long> pacientesGrupo;
    private Map<String, Long> pacientesStatus;
    private Map<String, Long> pacientesComoConheceu;

    @Data
    @Builder
    public static class ChartFinanceiroPoint {
        private String date;
        private BigDecimal recebidos;
        private BigDecimal aReceber;
    }

    @Data
    @Builder
    public static class ChartSessoesPoint {
        private String date;
        private long atendidos;
        private long desmarcados;
        private long faltantes;
    }

    @Data
    @Builder
    public static class ChartAgendamentosPoint {
        private String date;
        private long agendamentos;
    }
}
