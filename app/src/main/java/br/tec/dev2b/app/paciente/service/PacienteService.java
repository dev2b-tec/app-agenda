package br.tec.dev2b.app.paciente.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.paciente.dto.AtualizarPacienteDto;
import br.tec.dev2b.app.paciente.dto.CriarPacienteDto;
import br.tec.dev2b.app.paciente.dto.PacienteDto;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final EmpresaRepository empresaRepository;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PacienteDto criar(CriarPacienteDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Usuario usuario = dto.getUsuarioId() != null
                ? usuarioRepository.findById(dto.getUsuarioId()).orElse(null)
                : null;

        Paciente paciente = Paciente.builder()
                .empresa(empresa)
                .usuario(usuario)
                .nome(dto.getNome())
                .dataNascimento(dto.getDataNascimento())
                .telefone(dto.getTelefone())
                .genero(dto.getGenero())
                .plano(dto.getPlano())
                .numeroCarteirinha(dto.getNumeroCarteirinha())
                .grupo(dto.getGrupo())
                .comoConheceu(dto.getComoConheceu())
                .rg(dto.getRg())
                .cpf(dto.getCpf())
                .cep(dto.getCep())
                .email(dto.getEmail())
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .outrasInformacoes(dto.getOutrasInformacoes())
                .nomeResponsavel(dto.getNomeResponsavel())
                .dataNascimentoResp(dto.getDataNascimentoResp())
                .cpfResponsavel(dto.getCpfResponsavel())
                .telefoneResponsavel(dto.getTelefoneResponsavel())
                .build();

        return PacienteDto.from(pacienteRepository.save(paciente));
    }

    @Transactional(readOnly = true)
    public List<PacienteDto> listarPorEmpresa(UUID empresaId) {
        List<Paciente> pacientes = pacienteRepository.findByEmpresaIdOrderByNomeAsc(empresaId);
        Map<UUID, List<MovimentoFinanceiro>> movsPorPaciente = movimentoFinanceiroRepository
                .findByEmpresaIdOrderByDataVencimentoDesc(empresaId)
                .stream()
                .filter(m -> m.getPaciente() != null)
                .collect(Collectors.groupingBy(m -> m.getPaciente().getId()));
        return pacientes.stream().map(p -> {
            PacienteDto dto = PacienteDto.from(p);
            dto.setStatusPagamento(computarStatus(movsPorPaciente.getOrDefault(p.getId(), List.of())));
            return dto;
        }).toList();
    }

    private String computarStatus(List<MovimentoFinanceiro> movs) {
        if (movs.isEmpty()) return null;
        boolean todosQuitados = movs.stream().allMatch(m -> "PAGO".equalsIgnoreCase(m.getStatus()));
        return todosQuitados ? "QUITADO" : "EM_ABERTO";
    }

    @Transactional(readOnly = true)
    public List<PacienteDto> buscar(UUID empresaId, String busca) {
        if (busca == null || busca.trim().isEmpty()) {
            return listarPorEmpresa(empresaId);
        }
        List<Paciente> pacientes = pacienteRepository.buscarPorEmpresaETexto(empresaId, busca);
        Map<UUID, List<MovimentoFinanceiro>> movsPorPaciente = movimentoFinanceiroRepository
                .findByEmpresaIdOrderByDataVencimentoDesc(empresaId)
                .stream()
                .filter(m -> m.getPaciente() != null)
                .collect(Collectors.groupingBy(m -> m.getPaciente().getId()));
        return pacientes.stream().map(p -> {
            PacienteDto dto = PacienteDto.from(p);
            dto.setStatusPagamento(computarStatus(movsPorPaciente.getOrDefault(p.getId(), List.of())));
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<PacienteDto> filtrarPorStatus(UUID empresaId, String statusPagamento) {
        return pacienteRepository.findByEmpresaIdAndStatusPagamentoOrderByNomeAsc(empresaId, statusPagamento).stream()
                .map(PacienteDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PacienteDto buscarPorId(UUID id) {
        return pacienteRepository.findById(id)
                .map(PacienteDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));
    }

    @Transactional
    public PacienteDto atualizar(UUID id, AtualizarPacienteDto dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));

        if (dto.getNome() != null) paciente.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) paciente.setDataNascimento(dto.getDataNascimento());
        if (dto.getTelefone() != null) paciente.setTelefone(dto.getTelefone());
        if (dto.getGenero() != null) paciente.setGenero(dto.getGenero());
        if (dto.getPlano() != null) paciente.setPlano(dto.getPlano());
        if (dto.getNumeroCarteirinha() != null) paciente.setNumeroCarteirinha(dto.getNumeroCarteirinha());
        if (dto.getGrupo() != null) paciente.setGrupo(dto.getGrupo());
        if (dto.getComoConheceu() != null) paciente.setComoConheceu(dto.getComoConheceu());
        if (dto.getRg() != null) paciente.setRg(dto.getRg());
        if (dto.getCpf() != null) paciente.setCpf(dto.getCpf());
        if (dto.getCep() != null) paciente.setCep(dto.getCep());
        if (dto.getEmail() != null) paciente.setEmail(dto.getEmail());
        if (dto.getLogradouro() != null) paciente.setLogradouro(dto.getLogradouro());
        if (dto.getNumero() != null) paciente.setNumero(dto.getNumero());
        if (dto.getComplemento() != null) paciente.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null) paciente.setBairro(dto.getBairro());
        if (dto.getCidade() != null) paciente.setCidade(dto.getCidade());
        if (dto.getOutrasInformacoes() != null) paciente.setOutrasInformacoes(dto.getOutrasInformacoes());
        if (dto.getNomeResponsavel() != null) paciente.setNomeResponsavel(dto.getNomeResponsavel());
        if (dto.getDataNascimentoResp() != null) paciente.setDataNascimentoResp(dto.getDataNascimentoResp());
        if (dto.getCpfResponsavel() != null) paciente.setCpfResponsavel(dto.getCpfResponsavel());
        if (dto.getTelefoneResponsavel() != null) paciente.setTelefoneResponsavel(dto.getTelefoneResponsavel());
        if (dto.getStatusPagamento() != null) paciente.setStatusPagamento(dto.getStatusPagamento());
        if (dto.getSessoes() != null) paciente.setSessoes(dto.getSessoes());
        if (dto.getUsuarioId() != null) {
            usuarioRepository.findById(dto.getUsuarioId()).ifPresent(paciente::setUsuario);
        }

        return PacienteDto.from(pacienteRepository.save(paciente));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado: " + id);
        }
        pacienteRepository.deleteById(id);
    }
}
