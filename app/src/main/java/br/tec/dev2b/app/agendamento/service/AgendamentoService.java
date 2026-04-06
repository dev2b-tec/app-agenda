package br.tec.dev2b.app.agendamento.service;

import br.tec.dev2b.app.agendamento.dto.AgendamentoDto;
import br.tec.dev2b.app.agendamento.dto.AgendamentoServicoItemDto;
import br.tec.dev2b.app.agendamento.dto.AtualizarAgendamentoDto;
import br.tec.dev2b.app.agendamento.dto.CriarAgendamentoDto;
import br.tec.dev2b.app.linhadotempo.service.LinhaDoTempoService;
import br.tec.dev2b.app.agendamento.model.Agendamento;
import br.tec.dev2b.app.agendamento.model.AgendamentoServico;
import br.tec.dev2b.app.agendamento.repository.AgendamentoRepository;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import br.tec.dev2b.app.servico.model.Servico;
import br.tec.dev2b.app.servico.repository.ServicoRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final EmpresaRepository     empresaRepository;
    private final PacienteRepository    pacienteRepository;
    private final UsuarioRepository     usuarioRepository;
    private final ServicoRepository     servicoRepository;
    private final LinhaDoTempoService   linhaDoTempoService;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;

    @Transactional
    public AgendamentoDto criar(CriarAgendamentoDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        Paciente paciente = dto.getPacienteId() != null
                ? pacienteRepository.findById(dto.getPacienteId()).orElse(null)
                : null;

        Usuario usuario = dto.getUsuarioId() != null
                ? usuarioRepository.findById(dto.getUsuarioId()).orElse(null)
                : null;

        // Conflict check
        if (usuario != null) {
            List<Agendamento> conflitos = repository.findConflitos(usuario.getId(), dto.getInicio(), dto.getFim());
            if (!conflitos.isEmpty()) {
                throw new IllegalStateException("Horário já reservado para este profissional");
            }
        }

        Agendamento agendamento = Agendamento.builder()
                .empresa(empresa)
                .paciente(paciente)
                .pacienteNome(paciente != null ? paciente.getNome() : dto.getPacienteNome())
                .usuario(usuario)
                .usuarioNome(usuario != null ? usuario.getNome() : null)
                .inicio(dto.getInicio())
                .fim(dto.getFim())
                .sala(dto.getSala())
                .recorrente(dto.getRecorrente() != null ? dto.getRecorrente() : false)
                .observacoes(dto.getObservacoes())
                .cor(dto.getCor() != null ? dto.getCor() : "purple")
                .status("Aguardando")
                .valorTotal(dto.getValorTotal() != null ? dto.getValorTotal() : BigDecimal.ZERO)
                .valorRecebido(dto.getValorRecebido() != null ? dto.getValorRecebido() : BigDecimal.ZERO)
                .dataPagamento(dto.getDataPagamento())
                .metodoPagamento(dto.getMetodoPagamento())
                .build();

        if (dto.getServicos() != null) {
            for (AgendamentoServicoItemDto item : dto.getServicos()) {
                Servico servico = item.getServicoId() != null
                        ? servicoRepository.findById(item.getServicoId()).orElse(null)
                        : null;
                BigDecimal valorUnit = item.getValorUnitario() != null && item.getValorUnitario().compareTo(BigDecimal.ZERO) > 0
                        ? item.getValorUnitario()
                        : (servico != null && servico.getValor() != null ? servico.getValor() : BigDecimal.ZERO);
                agendamento.getServicos().add(AgendamentoServico.builder()
                        .agendamento(agendamento)
                        .servico(servico)
                        .servicoNome(item.getServicoNome() != null ? item.getServicoNome()
                                : (servico != null ? servico.getNome() : "Serviço"))
                        .quantidade(item.getQuantidade() != null ? item.getQuantidade() : 1)
                        .valorUnitario(valorUnit)
                        .build());
            }
        }

        // Auto-calculate valorTotal from servicos if not provided
        if (dto.getValorTotal() == null || dto.getValorTotal().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal total = agendamento.getServicos().stream()
                    .map(s -> s.getValorUnitario().multiply(BigDecimal.valueOf(s.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            agendamento.setValorTotal(total);
        }

        Agendamento saved = repository.save(agendamento);
        if (paciente != null) linhaDoTempoService.registrarAgendamento(saved);
        sincronizarMovimento(saved);
        return AgendamentoDto.from(saved);
    }

    private void sincronizarMovimento(Agendamento ag) {
        String statusMov = "ATENDIDO".equalsIgnoreCase(ag.getStatus()) || "Atendido".equals(ag.getStatus()) ? "PAGO"
                         : "CANCELADO".equalsIgnoreCase(ag.getStatus()) || "Cancelado".equals(ag.getStatus()) ? "CANCELADO"
                         : "EM_ABERTO";
        String titulo = ag.getPacienteNome() != null
                ? "Agendamento – " + ag.getPacienteNome()
                : "Agendamento";
        BigDecimal valorParcela = ag.getValorTotal() != null ? ag.getValorTotal() : BigDecimal.ZERO;
        BigDecimal valorPago    = ag.getValorRecebido() != null ? ag.getValorRecebido() : BigDecimal.ZERO;

        Optional<MovimentoFinanceiro> existente = movimentoFinanceiroRepository.findByReferenciaId(ag.getId());
        if (existente.isPresent()) {
            MovimentoFinanceiro m = existente.get();
            m.setTitulo(titulo);
            m.setValorParcela(valorParcela);
            m.setValorPago(valorPago);
            m.setStatus(statusMov);
            m.setDataVencimento(ag.getInicio().toLocalDate());
            m.setDataPagamento(ag.getDataPagamento());
            m.setMetodoPagamento(ag.getMetodoPagamento());
            movimentoFinanceiroRepository.save(m);
        } else {
            MovimentoFinanceiro m = MovimentoFinanceiro.builder()
                    .empresa(ag.getEmpresa())
                    .paciente(ag.getPaciente())
                    .usuario(ag.getUsuario())
                    .tipo("AGENDAMENTO")
                    .titulo(titulo)
                    .grupoId(UUID.randomUUID())
                    .numeroParcela(1)
                    .totalParcelas(1)
                    .valorParcela(valorParcela)
                    .valorPago(valorPago)
                    .dataVencimento(ag.getInicio().toLocalDate())
                    .dataPagamento(ag.getDataPagamento())
                    .status(statusMov)
                    .metodoPagamento(ag.getMetodoPagamento())
                    .referenciaId(ag.getId())
                    .build();
            movimentoFinanceiroRepository.save(m);
        }
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDto> listarPorEmpresa(UUID empresaId) {
        return repository.findByEmpresaIdOrderByInicioAsc(empresaId)
                .stream().map(AgendamentoDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDto> listarPorEmpresaEPeriodo(UUID empresaId, LocalDateTime inicio, LocalDateTime fim) {
        return repository.findByEmpresaIdAndPeriodo(empresaId, inicio, fim)
                .stream().map(AgendamentoDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgendamentoDto buscarPorId(UUID id) {
        return AgendamentoDto.from(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado")));
    }

    @Transactional
    public AgendamentoDto atualizar(UUID id, AtualizarAgendamentoDto dto) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));

        if (dto.getPacienteId() != null) {
            Paciente paciente = pacienteRepository.findById(dto.getPacienteId()).orElse(null);
            agendamento.setPaciente(paciente);
            agendamento.setPacienteNome(paciente != null ? paciente.getNome() : null);
        }

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId()).orElse(null);
            agendamento.setUsuario(usuario);
            agendamento.setUsuarioNome(usuario != null ? usuario.getNome() : null);
        }

        if (dto.getInicio() != null)      agendamento.setInicio(dto.getInicio());
        if (dto.getFim() != null)         agendamento.setFim(dto.getFim());
        if (dto.getStatus() != null)      agendamento.setStatus(dto.getStatus());
        if (dto.getSala() != null)        agendamento.setSala(dto.getSala());
        if (dto.getRecorrente() != null)  agendamento.setRecorrente(dto.getRecorrente());
        if (dto.getObservacoes() != null) agendamento.setObservacoes(dto.getObservacoes());
        if (dto.getCor() != null)         agendamento.setCor(dto.getCor());

        if (dto.getValorTotal() != null)      agendamento.setValorTotal(dto.getValorTotal());
        if (dto.getValorRecebido() != null)   agendamento.setValorRecebido(dto.getValorRecebido());
        if (dto.getDataPagamento() != null)   agendamento.setDataPagamento(dto.getDataPagamento());
        if (dto.getMetodoPagamento() != null) agendamento.setMetodoPagamento(dto.getMetodoPagamento());

        if (dto.getServicos() != null) {
            agendamento.getServicos().clear();
            for (AgendamentoServicoItemDto item : dto.getServicos()) {
                Servico servico = item.getServicoId() != null
                        ? servicoRepository.findById(item.getServicoId()).orElse(null)
                        : null;
                agendamento.getServicos().add(AgendamentoServico.builder()
                        .agendamento(agendamento)
                        .servico(servico)
                        .servicoNome(item.getServicoNome() != null ? item.getServicoNome()
                                : (servico != null ? servico.getNome() : "Serviço"))
                        .quantidade(item.getQuantidade() != null ? item.getQuantidade() : 1)
                        .valorUnitario(item.getValorUnitario() != null ? item.getValorUnitario() : BigDecimal.ZERO)
                        .build());
            }
        }

        Agendamento saved = repository.save(agendamento);
        if (saved.getPaciente() != null) linhaDoTempoService.registrarAgendamento(saved);
        sincronizarMovimento(saved);
        return AgendamentoDto.from(saved);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        linhaDoTempoService.removerPorReferencia(id);
        movimentoFinanceiroRepository.findByReferenciaId(id)
                .ifPresent(m -> movimentoFinanceiroRepository.deleteById(m.getId()));
        repository.deleteById(id);
    }
}
