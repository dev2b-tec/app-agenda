package br.tec.dev2b.app.paciente.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import br.tec.dev2b.app.financeiro.repository.MovimentoFinanceiroRepository;
import br.tec.dev2b.app.infra.minio.MinioService;
import br.tec.dev2b.app.paciente.dto.AtualizarPacienteDto;
import br.tec.dev2b.app.paciente.dto.ConcederAcessoDto;
import br.tec.dev2b.app.paciente.dto.CriarPacienteDto;
import br.tec.dev2b.app.paciente.dto.PacienteAcessoDto;
import br.tec.dev2b.app.paciente.dto.PacienteDto;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.model.PacienteAcesso;
import br.tec.dev2b.app.paciente.repository.PacienteAcessoRepository;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteAcessoRepository pacienteAcessoRepository;
    private final EmpresaRepository empresaRepository;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;
    private final UsuarioRepository usuarioRepository;
    private final MinioService minioService;

    @Transactional
    public PacienteDto criar(CriarPacienteDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Paciente paciente = Paciente.builder()
                .empresa(empresa)
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

        Paciente salvo = pacienteRepository.save(paciente);

        // Vincula automaticamente o usuário criador via paciente_acessos
        if (dto.getUsuarioId() != null) {
            usuarioRepository.findById(dto.getUsuarioId()).ifPresent(u ->
                pacienteAcessoRepository.save(PacienteAcesso.builder()
                        .paciente(salvo)
                        .usuario(u)
                        .build())
            );
        }

        return PacienteDto.from(salvo);
    }

    @Transactional(readOnly = true)
    public List<PacienteDto> listarPorEmpresa(UUID empresaId, UUID usuarioId) {
        List<Paciente> pacientes = usuarioId != null
                ? pacienteRepository.findByEmpresaIdAndUsuarioIdOrderByNomeAsc(empresaId, usuarioId)
                : pacienteRepository.findByEmpresaIdOrderByNomeAsc(empresaId);
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
    public List<PacienteDto> buscar(UUID empresaId, String busca, UUID usuarioId) {
        if (busca == null || busca.trim().isEmpty()) {
            return listarPorEmpresa(empresaId, usuarioId);
        }
        List<Paciente> pacientes = usuarioId != null
                ? pacienteRepository.buscarPorEmpresaUsuarioETexto(empresaId, usuarioId, busca)
                : pacienteRepository.buscarPorEmpresaETexto(empresaId, busca);
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

        return PacienteDto.from(pacienteRepository.save(paciente));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    // ── Acesso compartilhado ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PacienteAcessoDto> listarAcessos(UUID pacienteId) {
        return pacienteAcessoRepository.findByPacienteId(pacienteId).stream()
                .map(a -> {
                    PacienteAcessoDto dto = new PacienteAcessoDto();
                    dto.setUsuarioId(a.getUsuario().getId());
                    dto.setUsuarioNome(a.getUsuario().getNome());
                    return dto;
                }).toList();
    }

    @Transactional
    public void concederAcesso(UUID pacienteId, ConcederAcessoDto dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + pacienteId));
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + dto.getUsuarioId()));
        if (!pacienteAcessoRepository.existsByPacienteIdAndUsuarioId(pacienteId, usuario.getId())) {
            pacienteAcessoRepository.save(PacienteAcesso.builder()
                    .paciente(paciente)
                    .usuario(usuario)
                    .build());
        }
    }

    @Transactional
    public void revogarAcesso(UUID pacienteId, UUID usuarioId) {
        pacienteAcessoRepository.deleteByPacienteIdAndUsuarioId(pacienteId, usuarioId);
    }

    @Transactional
    public PacienteDto uploadFoto(UUID id, MultipartFile arquivo) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));

        String extensao = obterExtensao(arquivo.getOriginalFilename());
        String objectName = "pacientes/" + id + "/foto" + extensao;
        minioService.upload(minioService.getBucketFotos(), objectName, arquivo);
        String url = minioService.getPublicUrl(minioService.getBucketFotos(), objectName);

        paciente.setFotoUrl(url);
        return PacienteDto.from(pacienteRepository.save(paciente));
    }

    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) return ".jpg";
        return nomeArquivo.substring(nomeArquivo.lastIndexOf('.'));
    }
}
