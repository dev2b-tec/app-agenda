package br.tec.dev2b.app.ideia.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.ideia.dto.ComentarioDto;
import br.tec.dev2b.app.ideia.dto.CriarComentarioDto;
import br.tec.dev2b.app.ideia.dto.CriarIdeiaDto;
import br.tec.dev2b.app.ideia.dto.IdeiaDto;
import br.tec.dev2b.app.ideia.model.ComentarioIdeia;
import br.tec.dev2b.app.ideia.model.Ideia;
import br.tec.dev2b.app.ideia.model.VotoIdeia;
import br.tec.dev2b.app.ideia.repository.ComentarioIdeiaRepository;
import br.tec.dev2b.app.ideia.repository.IdeiaRepository;
import br.tec.dev2b.app.ideia.repository.VotoIdeiaRepository;
import br.tec.dev2b.app.infra.minio.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeiaService {

    private final IdeiaRepository ideiaRepository;
    private final VotoIdeiaRepository votoIdeiaRepository;
    private final ComentarioIdeiaRepository comentarioIdeiaRepository;
    private final EmpresaRepository empresaRepository;
    private final MinioService minioService;

    // ── Listar ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<IdeiaDto> listar(UUID empresaId, String filtro, int page, int size) {
        var pageable = PageRequest.of(page, size);
        Page<Ideia> pageResult = switch (filtro == null ? "todas" : filtro) {
            case "novas" -> ideiaRepository.findBySituacaoInOrderByNumeroDesc(List.of("NOVA", "MODERACAO"), pageable);
            case "em_estudo" -> ideiaRepository.findBySituacaoOrderByNumeroDesc("EM_ESTUDO", pageable);
            case "desenvolvidas" -> ideiaRepository.findBySituacaoOrderByNumeroDesc("DESENVOLVIDA", pageable);
            case "minhas" -> ideiaRepository.findByEmpresaIdOrderByNumeroDesc(empresaId, pageable);
            default -> ideiaRepository.findAllByOrderByNumeroDesc(pageable);
        };
        return pageResult.map(ideia -> toDto(ideia, empresaId));
    }

    // ── Buscar por ID ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public IdeiaDto buscarPorId(UUID id, UUID empresaId) {
        Ideia ideia = ideiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));
        return toDto(ideia, empresaId);
    }

    // ── Criar ─────────────────────────────────────────────────────────────────

    @Transactional
    public IdeiaDto criar(CriarIdeiaDto dto, List<MultipartFile> imagens) {
        String nomeEmpresa = empresaRepository.findById(dto.empresaId())
                .map(Empresa::getNomeComercial)
                .orElse("Empresa");

        Ideia ideia = Ideia.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .empresaId(dto.empresaId())
                .nomeEmpresa(nomeEmpresa)
                .categorias(dto.categorias() != null ? new ArrayList<>(dto.categorias()) : new ArrayList<>())
                .build();

        ideia = ideiaRepository.save(ideia);

        // Upload de imagens
        if (imagens != null && !imagens.isEmpty()) {
            String bucket = minioService.getBucketFotos();
            List<String> urls = new ArrayList<>();
            for (MultipartFile img : imagens) {
                try {
                    String ext = obterExtensao(img.getOriginalFilename());
                    String objectName = "ideias/" + ideia.getId() + "/" + UUID.randomUUID() + ext;
                    minioService.upload(bucket, objectName, img);
                    urls.add(minioService.getPublicUrl(bucket, objectName));
                } catch (Exception e) {
                    log.warn("[IdeiaService] falha ao fazer upload de imagem: {}", e.getMessage());
                }
            }
            ideia.setImageUrls(urls);
            ideia = ideiaRepository.save(ideia);
        }

        return toDto(ideia, dto.empresaId());
    }

    // ── Votar ─────────────────────────────────────────────────────────────────

    @Transactional
    public IdeiaDto votar(UUID ideiaId, UUID empresaId) {
        Ideia ideia = ideiaRepository.findById(ideiaId)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + ideiaId));

        Optional<VotoIdeia> existente = votoIdeiaRepository.findByIdeiaIdAndEmpresaId(ideiaId, empresaId);
        if (existente.isPresent()) {
            votoIdeiaRepository.delete(existente.get());
        } else {
            votoIdeiaRepository.save(VotoIdeia.builder()
                    .ideiaId(ideiaId)
                    .empresaId(empresaId)
                    .build());
        }
        return toDto(ideia, empresaId);
    }

    // ── Comentários ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ComentarioDto> listarComentarios(UUID ideiaId) {
        return comentarioIdeiaRepository.findByIdeiaIdOrderByCreatedAtAsc(ideiaId)
                .stream()
                .map(this::toComentarioDto)
                .toList();
    }

    @Transactional
    public ComentarioDto comentar(UUID ideiaId, CriarComentarioDto dto) {
        if (!ideiaRepository.existsById(ideiaId)) {
            throw new IllegalArgumentException("Ideia não encontrada: " + ideiaId);
        }
        String nomeEmpresa = empresaRepository.findById(dto.empresaId())
                .map(Empresa::getNomeComercial)
                .orElse("Empresa");

        ComentarioIdeia comentario = ComentarioIdeia.builder()
                .ideiaId(ideiaId)
                .empresaId(dto.empresaId())
                .nomeEmpresa(nomeEmpresa)
                .texto(dto.texto())
                .build();

        return toComentarioDto(comentarioIdeiaRepository.save(comentario));
    }

    // ── Mappings ──────────────────────────────────────────────────────────────

    private IdeiaDto toDto(Ideia ideia, UUID empresaId) {
        long totalVotos = votoIdeiaRepository.countByIdeiaId(ideia.getId());
        long totalComentarios = comentarioIdeiaRepository.countByIdeiaId(ideia.getId());
        boolean jaVotou = empresaId != null &&
                votoIdeiaRepository.existsByIdeiaIdAndEmpresaId(ideia.getId(), empresaId);

        return new IdeiaDto(
                ideia.getId(),
                ideia.getNumero(),
                ideia.getTitulo(),
                ideia.getDescricao(),
                ideia.getSituacao(),
                ideia.getEmpresaId(),
                ideia.getNomeEmpresa(),
                Boolean.TRUE.equals(ideia.getIdeiaLegal()),
                totalVotos,
                totalComentarios,
                jaVotou,
                ideia.getCategorias(),
                ideia.getImageUrls(),
                ideia.getCreatedAt()
        );
    }

    private ComentarioDto toComentarioDto(ComentarioIdeia c) {
        return new ComentarioDto(c.getId(), c.getIdeiaId(), c.getEmpresaId(), c.getNomeEmpresa(), c.getTexto(), c.getCreatedAt());
    }

    private String obterExtensao(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
