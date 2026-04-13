package br.tec.dev2b.app.ideia.controller;

import br.tec.dev2b.app.ideia.dto.ComentarioDto;
import br.tec.dev2b.app.ideia.dto.CriarComentarioDto;
import br.tec.dev2b.app.ideia.dto.CriarIdeiaDto;
import br.tec.dev2b.app.ideia.dto.IdeiaDto;
import br.tec.dev2b.app.ideia.service.IdeiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ideias")
@RequiredArgsConstructor
public class IdeiaController {

    private final IdeiaService ideiaService;

    /** Listar ideas com filtro e paginação */
    @GetMapping
    public ResponseEntity<Page<IdeiaDto>> listar(
            @RequestParam UUID empresaId,
            @RequestParam(defaultValue = "todas") String filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(ideiaService.listar(empresaId, filtro, page, size));
    }

    /** Buscar ideia por ID */
    @GetMapping("/{id}")
    public ResponseEntity<IdeiaDto> buscarPorId(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(ideiaService.buscarPorId(id, empresaId));
    }

    /** Criar nova ideia (multipart com imagens opcionais) */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IdeiaDto> criar(
            @RequestParam String titulo,
            @RequestParam(required = false) String descricao,
            @RequestParam UUID empresaId,
            @RequestParam(required = false) List<String> categorias,
            @RequestParam(required = false) List<MultipartFile> imagens
    ) {
        var dto = new CriarIdeiaDto(titulo, descricao, empresaId, categorias);
        return ResponseEntity.status(HttpStatus.CREATED).body(ideiaService.criar(dto, imagens));
    }

    /** Votar / desvota na ideia */
    @PostMapping("/{id}/votar")
    public ResponseEntity<IdeiaDto> votar(
            @PathVariable UUID id,
            @RequestParam UUID empresaId
    ) {
        return ResponseEntity.ok(ideiaService.votar(id, empresaId));
    }

    /** Listar comentários */
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioDto>> listarComentarios(@PathVariable UUID id) {
        return ResponseEntity.ok(ideiaService.listarComentarios(id));
    }

    /** Adicionar comentário */
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioDto> comentar(
            @PathVariable UUID id,
            @RequestBody CriarComentarioDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ideiaService.comentar(id, dto));
    }
}
