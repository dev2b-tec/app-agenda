package br.tec.dev2b.app.financeiro.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.financeiro.dto.AtualizarMovimentoDto;
import br.tec.dev2b.app.financeiro.dto.CriarMovimentoDto;
import br.tec.dev2b.app.financeiro.dto.MovimentoFinanceiroDto;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimentoFinanceiroService {

    private final MovimentoFinanceiroRepository repository;
    private final EmpresaRepository empresaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public List<MovimentoFinanceiroDto> criar(CriarMovimentoDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        Paciente paciente = dto.getPacienteId() != null
                ? pacienteRepository.findById(dto.getPacienteId()).orElse(null)
                : null;

        Usuario usuario = dto.getUsuarioId() != null
                ? usuarioRepository.findById(dto.getUsuarioId()).orElse(null)
                : null;

        int nParcelas = Math.max(1, dto.getTotalParcelas() != null ? dto.getTotalParcelas() : 1);
        BigDecimal valorTotal = dto.getValorTotal() != null ? dto.getValorTotal() : BigDecimal.ZERO;
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(nParcelas), 2, RoundingMode.HALF_UP);

        UUID grupoId = UUID.randomUUID();
        List<MovimentoFinanceiro> criados = new ArrayList<>();

        for (int i = 0; i < nParcelas; i++) {
            LocalDate vencimento = dto.getDataPrimeiroVencimento() != null
                    ? dto.getDataPrimeiroVencimento().plusMonths(i)
                    : LocalDate.now().plusMonths(i);

            MovimentoFinanceiro m = MovimentoFinanceiro.builder()
                    .empresa(empresa)
                    .paciente(paciente)
                    .usuario(usuario)
                    .tipo(dto.getTipo() != null ? dto.getTipo() : "MENSALIDADE")
                    .titulo(dto.getTitulo())
                    .grupoId(grupoId)
                    .numeroParcela(i + 1)
                    .totalParcelas(nParcelas)
                    .valorParcela(valorParcela)
                    .valorPago(BigDecimal.ZERO)
                    .dataVencimento(vencimento)
                    .status("EM_ABERTO")
                    .observacao(dto.getObservacao())
                    .build();
            criados.add(repository.save(m));
        }

        return criados.stream().map(MovimentoFinanceiroDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentoFinanceiroDto> listarPorEmpresa(UUID empresaId) {
        return repository.findByEmpresaIdOrderByDataVencimentoDesc(empresaId)
                .stream().map(MovimentoFinanceiroDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentoFinanceiroDto> listarPorEmpresaETipo(UUID empresaId, String tipo) {
        return repository.findByEmpresaIdAndTipoOrderByDataVencimentoDesc(empresaId, tipo)
                .stream().map(MovimentoFinanceiroDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentoFinanceiroDto> listarPorPaciente(UUID pacienteId) {
        return repository.findByPacienteIdOrderByDataVencimentoDesc(pacienteId)
                .stream().map(MovimentoFinanceiroDto::from).collect(Collectors.toList());
    }

    @Transactional
    public MovimentoFinanceiroDto atualizar(UUID id, AtualizarMovimentoDto dto) {
        MovimentoFinanceiro m = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimento não encontrado: " + id));

        if (dto.getTitulo() != null) m.setTitulo(dto.getTitulo());
        if (dto.getStatus() != null) m.setStatus(dto.getStatus());
        if (dto.getValorPago() != null) m.setValorPago(dto.getValorPago());
        if (dto.getDataPagamento() != null) m.setDataPagamento(dto.getDataPagamento());
        if (dto.getMetodoPagamento() != null) m.setMetodoPagamento(dto.getMetodoPagamento());
        if (dto.getObservacao() != null) m.setObservacao(dto.getObservacao());

        return MovimentoFinanceiroDto.from(repository.save(m));
    }

    @Transactional
    public void deletarParcela(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deletarGrupo(UUID grupoId) {
        repository.deleteByGrupoId(grupoId);
    }

    @Transactional
    public void deletarParcelasFuturas(UUID id) {
        MovimentoFinanceiro ref = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimento não encontrado: " + id));

        List<MovimentoFinanceiro> grupo = repository.findByGrupoIdOrderByNumeroParcela(ref.getGrupoId());
        grupo.stream()
                .filter(m -> m.getNumeroParcela() >= ref.getNumeroParcela())
                .forEach(m -> repository.deleteById(m.getId()));
    }
}
