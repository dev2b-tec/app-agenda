package br.tec.dev2b.app.anamnese.service;

import br.tec.dev2b.app.anamnese.dto.RespostaAnamneseDto;
import br.tec.dev2b.app.anamnese.dto.SalvarRespostaAnamneseDto;
import br.tec.dev2b.app.anamnese.model.Anamnese;
import br.tec.dev2b.app.anamnese.model.Pergunta;
import br.tec.dev2b.app.anamnese.model.RespostaAnamnese;
import br.tec.dev2b.app.anamnese.model.RespostaAnamneseItem;
import br.tec.dev2b.app.anamnese.repository.AnamneseRepository;
import br.tec.dev2b.app.anamnese.repository.PerguntaRepository;
import br.tec.dev2b.app.anamnese.repository.RespostaAnamneseRepository;
import br.tec.dev2b.app.linhadotempo.service.LinhaDoTempoService;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RespostaAnamneseService {

    private final RespostaAnamneseRepository respostaAnamneseRepository;
    private final AnamneseRepository anamneseRepository;
    private final PacienteRepository pacienteRepository;
    private final PerguntaRepository perguntaRepository;
    private final LinhaDoTempoService linhaDoTempoService;

    @Transactional(readOnly = true)
    public Optional<RespostaAnamneseDto> buscar(UUID pacienteId, UUID anamneseId) {
        return respostaAnamneseRepository.findByPacienteIdAndAnamneseId(pacienteId, anamneseId)
                .map(RespostaAnamneseDto::from);
    }

    @Transactional
    public RespostaAnamneseDto salvar(SalvarRespostaAnamneseDto dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + dto.getPacienteId()));

        Anamnese anamnese = anamneseRepository.findById(dto.getAnamneseId())
                .orElseThrow(() -> new IllegalArgumentException("Anamnese não encontrada: " + dto.getAnamneseId()));

        RespostaAnamnese resposta = respostaAnamneseRepository
                .findByPacienteIdAndAnamneseId(dto.getPacienteId(), dto.getAnamneseId())
                .orElseGet(() -> RespostaAnamnese.builder()
                        .paciente(paciente)
                        .anamnese(anamnese)
                        .build());

        resposta.setProfissional(dto.getProfissional());
        resposta.setData(LocalDate.now());
        resposta.getItens().clear();

        if (dto.getItens() != null) {
            for (SalvarRespostaAnamneseDto.ItemDto itemDto : dto.getItens()) {
                Pergunta pergunta = perguntaRepository.findById(itemDto.getPerguntaId())
                        .orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada: " + itemDto.getPerguntaId()));

                RespostaAnamneseItem item = RespostaAnamneseItem.builder()
                        .respostaAnamnese(resposta)
                        .pergunta(pergunta)
                        .opcao(itemDto.getOpcao() != null ? itemDto.getOpcao() : "NENHUM")
                        .texto(itemDto.getTexto())
                        .build();
                resposta.getItens().add(item);
            }
        }

        RespostaAnamnese saved = respostaAnamneseRepository.save(resposta);
        linhaDoTempoService.registrarAnamnese(saved, anamnese.getTitulo());
        return RespostaAnamneseDto.from(saved);
    }
}
