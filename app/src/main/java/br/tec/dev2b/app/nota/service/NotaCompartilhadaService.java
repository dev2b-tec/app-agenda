package br.tec.dev2b.app.nota.service;

import br.tec.dev2b.app.nota.dto.CriarNotaDto;
import br.tec.dev2b.app.nota.dto.NotaCompartilhadaDto;
import br.tec.dev2b.app.nota.model.NotaCompartilhada;
import br.tec.dev2b.app.nota.repository.NotaCompartilhadaRepository;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotaCompartilhadaService {

    private final NotaCompartilhadaRepository notaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public List<NotaCompartilhadaDto> listarPorPaciente(UUID pacienteId) {
        return notaRepository.findByPacienteIdOrderByCriadoEmDesc(pacienteId)
                .stream().map(NotaCompartilhadaDto::from).toList();
    }

    public NotaCompartilhadaDto criar(CriarNotaDto dto) {
        var paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
        var autor = usuarioRepository.findById(dto.getAutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        var nota = NotaCompartilhada.builder()
                .paciente(paciente)
                .autor(autor)
                .autorNome(dto.getAutorNome() != null ? dto.getAutorNome() : autor.getNome())
                .titulo(dto.getTitulo())
                .texto(dto.getTexto())
                .cor(dto.getCor() != null ? dto.getCor() : "#FEF9C3")
                .build();

        return NotaCompartilhadaDto.from(notaRepository.save(nota));
    }

    public void deletar(UUID id, UUID autorId) {
        var nota = notaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada"));
        if (!nota.getAutor().getId().equals(autorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode excluir esta nota");
        }
        notaRepository.delete(nota);
    }
}
