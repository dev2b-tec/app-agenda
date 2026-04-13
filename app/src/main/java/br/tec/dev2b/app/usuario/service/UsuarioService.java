package br.tec.dev2b.app.usuario.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.infra.minio.MinioService;
import br.tec.dev2b.app.usuario.dto.AtualizarUsuarioDto;
import br.tec.dev2b.app.usuario.dto.CriarUsuarioDto;
import br.tec.dev2b.app.usuario.dto.SyncUsuarioDto;
import br.tec.dev2b.app.usuario.dto.UsuarioDto;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final MinioService minioService;
    private final br.tec.dev2b.app.agenda.repository.AgendaRepository agendaRepository;

    @Transactional
    public UsuarioDto criar(CriarUsuarioDto dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + dto.getEmail());
        }

        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .keycloakId(dto.getKeycloakId())
                .telefone(dto.getTelefone())
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .tipo(dto.getTipo())
                .conselho(dto.getConselho())
                .numeroConselho(dto.getNumeroConselho())
                .especialidade(dto.getEspecialidade())
                .empresa(empresa)
                .build();

        return UsuarioDto.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listarPorEmpresa(UUID empresaId) {
        return usuarioRepository.findByEmpresaId(empresaId).stream().map(UsuarioDto::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDto buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .map(UsuarioDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public UsuarioDto buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(UsuarioDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + email));
    }

    @Transactional(readOnly = true)
    public UsuarioDto buscarPorKeycloakId(String keycloakId) {
        return usuarioRepository.findByKeycloakId(keycloakId)
                .map(UsuarioDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + keycloakId));
    }

    @Transactional
    public UsuarioDto atualizar(UUID id, AtualizarUsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));

        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getTelefone() != null) usuario.setTelefone(dto.getTelefone());
        if (dto.getCep() != null) usuario.setCep(dto.getCep());
        if (dto.getLogradouro() != null) usuario.setLogradouro(dto.getLogradouro());
        if (dto.getNumero() != null) usuario.setNumero(dto.getNumero());
        if (dto.getComplemento() != null) usuario.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null) usuario.setBairro(dto.getBairro());
        if (dto.getCidade() != null) usuario.setCidade(dto.getCidade());
        if (dto.getTipo() != null) usuario.setTipo(dto.getTipo());
        if (dto.getConselho() != null) usuario.setConselho(dto.getConselho());
        if (dto.getNumeroConselho() != null) usuario.setNumeroConselho(dto.getNumeroConselho());
        if (dto.getEspecialidade() != null) usuario.setEspecialidade(dto.getEspecialidade());
        if (dto.getGenero() != null) usuario.setGenero(dto.getGenero());
        if (dto.getDuracaoSessao() != null) usuario.setDuracaoSessao(dto.getDuracaoSessao());
        if (dto.getPeriodoMinimo() != null) usuario.setPeriodoMinimo(dto.getPeriodoMinimo());
        if (dto.getPeriodoMaximo() != null) usuario.setPeriodoMaximo(dto.getPeriodoMaximo());
        if (dto.getTempoAntecedencia() != null) usuario.setTempoAntecedencia(dto.getTempoAntecedencia());
        if (dto.getDisponivel() != null) usuario.setDisponivel(dto.getDisponivel());
        if (dto.getTelefoneComercial() != null) usuario.setTelefoneComercial(dto.getTelefoneComercial());
        if (dto.getObservacoes() != null) usuario.setObservacoes(dto.getObservacoes());
        if (dto.getDescricaoAtuacao()   != null) usuario.setDescricaoAtuacao(dto.getDescricaoAtuacao());
        if (dto.getCursosCertificacoes()!= null) usuario.setCursosCertificacoes(dto.getCursosCertificacoes());
        if (dto.getFaixaPreco()         != null) usuario.setFaixaPreco(dto.getFaixaPreco());
        if (dto.getModoAtendimento()    != null) usuario.setModoAtendimento(dto.getModoAtendimento());
        if (dto.getAceitaConvenio()     != null) usuario.setAceitaConvenio(dto.getAceitaConvenio());
        if (dto.getInstagram()          != null) usuario.setInstagram(dto.getInstagram());
        if (dto.getFacebook()           != null) usuario.setFacebook(dto.getFacebook());
        if (dto.getLinkedin()           != null) usuario.setLinkedin(dto.getLinkedin());
        if (dto.getYoutube()            != null) usuario.setYoutube(dto.getYoutube());
        if (dto.getWebsiteLink()        != null) usuario.setWebsiteLink(dto.getWebsiteLink());
        if (dto.getWhatsapp()           != null) usuario.setWhatsapp(dto.getWhatsapp());
        if (dto.getPublicadoBuscador()  != null) usuario.setPublicadoBuscador(dto.getPublicadoBuscador());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));
            usuario.setEmpresa(empresa);
        }

        if (dto.getAgendaId() != null) {
            br.tec.dev2b.app.agenda.model.Agenda agenda = agendaRepository.findById(dto.getAgendaId())
                    .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada: " + dto.getAgendaId()));
            usuario.setAgenda(agenda);
        }

        return UsuarioDto.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public String uploadFoto(UUID id, MultipartFile file) {
        log.info("[uploadFoto] Iniciando upload de foto para usuário id={}", id);
        log.debug("[uploadFoto] Arquivo recebido: nome={}, tamanho={} bytes, contentType={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[uploadFoto] Usuário não encontrado: id={}", id);
                    return new IllegalArgumentException("Usuário não encontrado: " + id);
                });
        log.debug("[uploadFoto] Usuário encontrado: email={}", usuario.getEmail());

        String ext = obterExtensao(file.getOriginalFilename());
        String objectName = "usuarios/" + id + "/foto" + ext;
        String bucket = minioService.getBucketFotos();
        log.info("[uploadFoto] Enviando para MinIO bucket={} objectName={}", bucket, objectName);

        minioService.upload(bucket, objectName, file);
        log.info("[uploadFoto] Upload para MinIO concluído");

        // Store only the object path (not the presigned URL) — presigned URLs expire.
        // The /foto-url endpoint generates a fresh URL on demand.
        usuario.setFotoUrl(objectName);
        usuarioRepository.save(usuario);
        log.info("[uploadFoto] objectName salvo no banco para usuário id={}", id);

        String url = minioService.getPublicUrl(bucket, objectName);
        log.info("[uploadFoto] URL pública gerada para resposta: {}", url);
        return url;
    }

    @Transactional
    public String uploadAssinatura(UUID id, MultipartFile file) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));

        String ext = obterExtensao(file.getOriginalFilename());
        String objectName = "usuarios/" + id + "/assinatura" + ext;
        String bucket = minioService.getBucketDocumentos();

        minioService.upload(bucket, objectName, file);
        // Store only the object path (not the presigned URL) — presigned URLs expire.
        usuario.setAssinaturaUrl(objectName);
        usuarioRepository.save(usuario);
        return minioService.getPublicUrl(bucket, objectName);
    }

    @Transactional(readOnly = true)
    public String obterFotoUrl(UUID id) {
        log.info("[obterFotoUrl] Buscando URL de foto para usuário id={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[obterFotoUrl] Usuário não encontrado: id={}", id);
                    return new IllegalArgumentException("Usuário não encontrado: " + id);
                });

        if (usuario.getFotoUrl() == null || usuario.getFotoUrl().isEmpty()) {
            log.warn("[obterFotoUrl] Usuário id={} não possui foto cadastrada", id);
            throw new IllegalArgumentException("Usuário não possui foto cadastrada");
        }
        log.debug("[obterFotoUrl] fotoUrl armazenada no banco: {}", usuario.getFotoUrl());

        // fotoUrl may be a plain objectName (new records) or a legacy presigned URL (old records).
        // In both cases obterExtensaoUrl extracts the extension correctly.
        String fotoRef = usuario.getFotoUrl();
        String objectName = fotoRef.startsWith("usuarios/") && !fotoRef.contains("?") 
                ? fotoRef  // already an objectName
                : "usuarios/" + id + "/foto" + obterExtensaoUrl(fotoRef); // legacy presigned URL
        String bucket = minioService.getBucketFotos();
        log.info("[obterFotoUrl] Gerando URL pública para bucket={} objectName={}", bucket, objectName);

        String url = minioService.getPublicUrl(bucket, objectName);
        log.info("[obterFotoUrl] URL pública gerada: {}", url);
        return url;
    }

    private String obterExtensaoUrl(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.contains(".")) {
            return url.substring(url.lastIndexOf("."));
        }
        return ".jpg";
    }

    @Transactional
    public void deletar(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private String obterExtensao(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    @Transactional
    public UsuarioDto sync(SyncUsuarioDto dto) {
        return usuarioRepository.findByKeycloakId(dto.getKeycloakId())
                .or(() -> usuarioRepository.findByEmail(dto.getEmail())
                        .map(u -> {
                            u.setKeycloakId(dto.getKeycloakId());
                            return usuarioRepository.save(u);
                        }))
                .map(UsuarioDto::from)
                .orElseGet(() -> {
                    Empresa empresa = empresaRepository.save(
                            Empresa.builder().nomeComercial(dto.getNome()).build()
                    );
                    Usuario usuario = Usuario.builder()
                            .nome(dto.getNome())
                            .email(dto.getEmail())
                            .keycloakId(dto.getKeycloakId())
                            .empresa(empresa)
                            .build();
                    return UsuarioDto.from(usuarioRepository.save(usuario));
                });
    }

    @Transactional
    public UsuarioDto habilitarAgendaProfissional(UUID id, boolean habilitar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));

        if (habilitar && usuario.getAgenda() == null) {
            // Criar nova agenda com valores padrão
            br.tec.dev2b.app.agenda.model.Agenda agenda = br.tec.dev2b.app.agenda.model.Agenda.builder()
                    .segAbertura(java.time.LocalTime.of(8, 0))
                    .segFechamento(java.time.LocalTime.of(22, 0))
                    .terAbertura(java.time.LocalTime.of(8, 0))
                    .terFechamento(java.time.LocalTime.of(22, 0))
                    .quaAbertura(java.time.LocalTime.of(8, 0))
                    .quaFechamento(java.time.LocalTime.of(22, 0))
                    .quiAbertura(java.time.LocalTime.of(8, 0))
                    .quiFechamento(java.time.LocalTime.of(22, 0))
                    .sexAbertura(java.time.LocalTime.of(8, 0))
                    .sexFechamento(java.time.LocalTime.of(22, 0))
                    .sabAbertura(java.time.LocalTime.of(8, 0))
                    .sabFechamento(java.time.LocalTime.of(22, 0))
                    .domAbertura(java.time.LocalTime.of(8, 0))
                    .domFechamento(java.time.LocalTime.of(22, 0))
                    .almocoInicio(java.time.LocalTime.of(12, 0))
                    .almocoFim(java.time.LocalTime.of(13, 0))
                    .build();
            agenda = agendaRepository.save(agenda);
            usuario.setAgenda(agenda);
        } else if (!habilitar && usuario.getAgenda() != null) {
            // Desabilitar agenda (não deletar, apenas desvincular)
            usuario.setAgenda(null);
        }

        return UsuarioDto.from(usuarioRepository.save(usuario));
    }
}
