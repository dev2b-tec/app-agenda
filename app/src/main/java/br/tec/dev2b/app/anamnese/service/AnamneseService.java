package br.tec.dev2b.app.anamnese.service;

import br.tec.dev2b.app.anamnese.dto.*;
import br.tec.dev2b.app.anamnese.model.Anamnese;
import br.tec.dev2b.app.anamnese.model.Pergunta;
import br.tec.dev2b.app.anamnese.repository.AnamneseRepository;
import br.tec.dev2b.app.anamnese.repository.PerguntaRepository;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnamneseService {

    private final AnamneseRepository anamneseRepository;
    private final PerguntaRepository perguntaRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public AnamneseDto criar(CriarAnamneseDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Anamnese anamnese = Anamnese.builder()
                .titulo(dto.getTitulo())
                .empresa(empresa)
                .build();

        // Adicionar perguntas
        if (dto.getPerguntas() != null) {
            for (int i = 0; i < dto.getPerguntas().size(); i++) {
                CriarPerguntaDto perguntaDto = dto.getPerguntas().get(i);
                Pergunta pergunta = Pergunta.builder()
                        .texto(perguntaDto.getTexto())
                        .tipoResposta(perguntaDto.getTipoResposta())
                        .ordem(i)
                        .build();
                anamnese.addPergunta(pergunta);
            }
        }

        return AnamneseDto.from(anamneseRepository.save(anamnese));
    }

    @Transactional(readOnly = true)
    public List<AnamneseDto> listarPorEmpresa(UUID empresaId) {
        return anamneseRepository.findByEmpresaId(empresaId).stream()
                .map(AnamneseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnamneseDto buscarPorId(UUID id) {
        return anamneseRepository.findById(id)
                .map(AnamneseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Anamnese não encontrada: " + id));
    }

    @Transactional
    public AnamneseDto atualizar(UUID id, AtualizarAnamneseDto dto) {
        Anamnese anamnese = anamneseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anamnese não encontrada: " + id));

        if (dto.getTitulo() != null) {
            anamnese.setTitulo(dto.getTitulo());
        }

        if (dto.getPerguntas() != null) {
            // Remover perguntas antigas
            anamnese.getPerguntas().clear();

            // Adicionar novas perguntas
            for (int i = 0; i < dto.getPerguntas().size(); i++) {
                AtualizarPerguntaDto perguntaDto = dto.getPerguntas().get(i);
                
                Pergunta pergunta;
                if (perguntaDto.getId() != null) {
                    // Atualizar pergunta existente
                    pergunta = perguntaRepository.findById(perguntaDto.getId())
                            .orElse(Pergunta.builder().build());
                } else {
                    // Nova pergunta
                    pergunta = Pergunta.builder().build();
                }
                
                pergunta.setTexto(perguntaDto.getTexto());
                pergunta.setTipoResposta(perguntaDto.getTipoResposta());
                pergunta.setOrdem(i);
                pergunta.setAtiva(perguntaDto.getAtiva() != null ? perguntaDto.getAtiva() : true);
                
                anamnese.addPergunta(pergunta);
            }
        }

        return AnamneseDto.from(anamneseRepository.save(anamnese));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!anamneseRepository.existsById(id)) {
            throw new IllegalArgumentException("Anamnese não encontrada: " + id);
        }
        anamneseRepository.deleteById(id);
    }
}
