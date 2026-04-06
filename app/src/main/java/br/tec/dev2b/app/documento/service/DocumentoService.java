package br.tec.dev2b.app.documento.service;

import br.tec.dev2b.app.documento.dto.AtualizarDocumentoDto;
import br.tec.dev2b.app.documento.dto.CriarDocumentoDto;
import br.tec.dev2b.app.documento.dto.DocumentoDto;
import br.tec.dev2b.app.documento.model.Documento;
import br.tec.dev2b.app.documento.repository.DocumentoRepository;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.linhadotempo.service.LinhaDoTempoService;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final EmpresaRepository empresaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final LinhaDoTempoService linhaDoTempoService;

    @Transactional(readOnly = true)
    public List<DocumentoDto> listarPorPaciente(UUID pacienteId) {
        return documentoRepository.findByPacienteIdOrderByCreatedAtDesc(pacienteId)
                .stream().map(DocumentoDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoDto buscarPorId(UUID id) {
        return documentoRepository.findById(id)
                .map(DocumentoDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Documento n\u00e3o encontrado"));
    }

    @Transactional
    public DocumentoDto criar(CriarDocumentoDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        Usuario usuario = dto.getUsuarioId() != null
                ? usuarioRepository.findById(dto.getUsuarioId()).orElse(null)
                : null;

        Documento doc = Documento.builder()
                .empresa(empresa)
                .paciente(paciente)
                .usuario(usuario)
                .titulo(dto.getTitulo())
                .conteudo(dto.getConteudo())
                .tipo(dto.getTipo() != null ? dto.getTipo() : "branco")
                .permProf(dto.getPermProf() != null ? dto.getPermProf() : true)
                .permAssist(dto.getPermAssist() != null ? dto.getPermAssist() : true)
                .build();

        Documento salvo = documentoRepository.save(doc);
        linhaDoTempoService.registrarDocumento(salvo);
        return DocumentoDto.from(salvo);
    }

    @Transactional
    public DocumentoDto atualizar(UUID id, AtualizarDocumentoDto dto) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));

        Usuario usuario = dto.getUsuarioId() != null
                ? usuarioRepository.findById(dto.getUsuarioId()).orElse(doc.getUsuario())
                : doc.getUsuario();

        if (dto.getTitulo() != null) doc.setTitulo(dto.getTitulo());
        if (dto.getConteudo() != null) doc.setConteudo(dto.getConteudo());
        if (dto.getTipo() != null) doc.setTipo(dto.getTipo());
        if (dto.getPermProf() != null) doc.setPermProf(dto.getPermProf());
        if (dto.getPermAssist() != null) doc.setPermAssist(dto.getPermAssist());
        doc.setUsuario(usuario);

        Documento salvo = documentoRepository.save(doc);
        linhaDoTempoService.registrarDocumento(salvo);
        return DocumentoDto.from(salvo);
    }

    @Transactional
    public void excluir(UUID id) {
        linhaDoTempoService.removerPorReferencia(id);
        documentoRepository.deleteById(id);
    }
}
