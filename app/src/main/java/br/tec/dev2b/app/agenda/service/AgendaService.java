package br.tec.dev2b.app.agenda.service;

import br.tec.dev2b.app.agenda.dto.AgendaDto;
import br.tec.dev2b.app.agenda.dto.AtualizarAgendaDto;
import br.tec.dev2b.app.agenda.model.Agenda;
import br.tec.dev2b.app.agenda.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    @Transactional
    public AgendaDto criar() {
        Agenda agenda = Agenda.builder()
                .segAbertura(LocalTime.of(8, 0))
                .segFechamento(LocalTime.of(22, 0))
                .terAbertura(LocalTime.of(8, 0))
                .terFechamento(LocalTime.of(22, 0))
                .quaAbertura(LocalTime.of(8, 0))
                .quaFechamento(LocalTime.of(22, 0))
                .quiAbertura(LocalTime.of(8, 0))
                .quiFechamento(LocalTime.of(22, 0))
                .sexAbertura(LocalTime.of(8, 0))
                .sexFechamento(LocalTime.of(22, 0))
                .sabAbertura(LocalTime.of(8, 0))
                .sabFechamento(LocalTime.of(22, 0))
                .domAbertura(LocalTime.of(8, 0))
                .domFechamento(LocalTime.of(22, 0))
                .almocoInicio(LocalTime.of(12, 0))
                .almocoFim(LocalTime.of(13, 0))
                .build();

        return AgendaDto.from(agendaRepository.save(agenda));
    }

    @Transactional(readOnly = true)
    public List<AgendaDto> listar() {
        return agendaRepository.findAll().stream().map(AgendaDto::from).toList();
    }

    @Transactional(readOnly = true)
    public AgendaDto buscarPorId(UUID id) {
        return agendaRepository.findById(id)
                .map(AgendaDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada: " + id));
    }

    @Transactional
    public AgendaDto atualizar(UUID id, AtualizarAgendaDto dto) {
        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada: " + id));

        if (dto.getControleComissoes() != null) agenda.setControleComissoes(dto.getControleComissoes());
        if (dto.getRecursoDesativadoComissoes() != null) agenda.setRecursoDesativadoComissoes(dto.getRecursoDesativadoComissoes());
        if (dto.getOverbookingProfissionais() != null) agenda.setOverbookingProfissionais(dto.getOverbookingProfissionais());
        if (dto.getRecursoDesativadoOverbooking() != null) agenda.setRecursoDesativadoOverbooking(dto.getRecursoDesativadoOverbooking());
        if (dto.getFilaEspera() != null) agenda.setFilaEspera(dto.getFilaEspera());
        if (dto.getRecursoDesativadoFila() != null) agenda.setRecursoDesativadoFila(dto.getRecursoDesativadoFila());
        if (dto.getBloquearEdicaoEvolucao() != null) agenda.setBloquearEdicaoEvolucao(dto.getBloquearEdicaoEvolucao());
        if (dto.getRecursoDesativadoEvolucao() != null) agenda.setRecursoDesativadoEvolucao(dto.getRecursoDesativadoEvolucao());

        if (dto.getSegAbertura() != null) agenda.setSegAbertura(dto.getSegAbertura());
        if (dto.getSegFechamento() != null) agenda.setSegFechamento(dto.getSegFechamento());
        if (dto.getSegAberto() != null) agenda.setSegAberto(dto.getSegAberto());

        if (dto.getTerAbertura() != null) agenda.setTerAbertura(dto.getTerAbertura());
        if (dto.getTerFechamento() != null) agenda.setTerFechamento(dto.getTerFechamento());
        if (dto.getTerAberto() != null) agenda.setTerAberto(dto.getTerAberto());

        if (dto.getQuaAbertura() != null) agenda.setQuaAbertura(dto.getQuaAbertura());
        if (dto.getQuaFechamento() != null) agenda.setQuaFechamento(dto.getQuaFechamento());
        if (dto.getQuaAberto() != null) agenda.setQuaAberto(dto.getQuaAberto());

        if (dto.getQuiAbertura() != null) agenda.setQuiAbertura(dto.getQuiAbertura());
        if (dto.getQuiFechamento() != null) agenda.setQuiFechamento(dto.getQuiFechamento());
        if (dto.getQuiAberto() != null) agenda.setQuiAberto(dto.getQuiAberto());

        if (dto.getSexAbertura() != null) agenda.setSexAbertura(dto.getSexAbertura());
        if (dto.getSexFechamento() != null) agenda.setSexFechamento(dto.getSexFechamento());
        if (dto.getSexAberto() != null) agenda.setSexAberto(dto.getSexAberto());

        if (dto.getSabAbertura() != null) agenda.setSabAbertura(dto.getSabAbertura());
        if (dto.getSabFechamento() != null) agenda.setSabFechamento(dto.getSabFechamento());
        if (dto.getSabAberto() != null) agenda.setSabAberto(dto.getSabAberto());

        if (dto.getDomAbertura() != null) agenda.setDomAbertura(dto.getDomAbertura());
        if (dto.getDomFechamento() != null) agenda.setDomFechamento(dto.getDomFechamento());
        if (dto.getDomAberto() != null) agenda.setDomAberto(dto.getDomAberto());

        if (dto.getAlmocoInicio() != null) agenda.setAlmocoInicio(dto.getAlmocoInicio());
        if (dto.getAlmocoFim() != null) agenda.setAlmocoFim(dto.getAlmocoFim());
        if (dto.getAtivarHorarioAlmoco() != null) agenda.setAtivarHorarioAlmoco(dto.getAtivarHorarioAlmoco());
        if (dto.getExibirProjecao() != null) agenda.setExibirProjecao(dto.getExibirProjecao());

        return AgendaDto.from(agendaRepository.save(agenda));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!agendaRepository.existsById(id)) {
            throw new IllegalArgumentException("Agenda não encontrada: " + id);
        }
        agendaRepository.deleteById(id);
    }
}
