package br.tec.dev2b.app.evolucao.service;

import br.tec.dev2b.app.evolucao.dto.AtualizarEvolucaoDto;
import br.tec.dev2b.app.evolucao.dto.CriarEvolucaoDto;
import br.tec.dev2b.app.evolucao.dto.EvolucaoDto;
import br.tec.dev2b.app.evolucao.model.Evolucao;
import br.tec.dev2b.app.evolucao.repository.EvolucaoRepository;
import br.tec.dev2b.app.linhadotempo.service.LinhaDoTempoService;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvolucaoService {

    private final EvolucaoRepository evolucaoRepository;
    private final PacienteRepository pacienteRepository;
    private final LinhaDoTempoService linhaDoTempoService;

    @Transactional
    public EvolucaoDto criar(CriarEvolucaoDto dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + dto.getPacienteId()));

        Evolucao evolucao = Evolucao.builder()
                .paciente(paciente)
                .titulo(dto.getTitulo())
                .profissional(dto.getProfissional())
                .data(dto.getData())
                .assinado(dto.getAssinado() != null ? dto.getAssinado() : false)
                .resumoAi(dto.getResumoAi())
                .comentariosGerais(dto.getComentariosGerais())
                .conduta(dto.getConduta())
                .examesRealizados(dto.getExamesRealizados())
                .prescricao(dto.getPrescricao())
                .build();

        Evolucao saved = evolucaoRepository.save(evolucao);
        linhaDoTempoService.registrarEvolucao(saved);
        return EvolucaoDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<EvolucaoDto> listarPorPaciente(UUID pacienteId) {
        return evolucaoRepository.findByPacienteIdOrderByDataDesc(pacienteId).stream()
                .map(EvolucaoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvolucaoDto buscarPorId(UUID id) {
        return evolucaoRepository.findById(id)
                .map(EvolucaoDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Evolução não encontrada: " + id));
    }

    @Transactional
    public EvolucaoDto atualizar(UUID id, AtualizarEvolucaoDto dto) {
        Evolucao evolucao = evolucaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evolução não encontrada: " + id));

        if (dto.getTitulo() != null) evolucao.setTitulo(dto.getTitulo());
        if (dto.getProfissional() != null) evolucao.setProfissional(dto.getProfissional());
        if (dto.getData() != null) evolucao.setData(dto.getData());
        if (dto.getAssinado() != null) evolucao.setAssinado(dto.getAssinado());
        if (dto.getResumoAi() != null) evolucao.setResumoAi(dto.getResumoAi());
        if (dto.getComentariosGerais() != null) evolucao.setComentariosGerais(dto.getComentariosGerais());
        if (dto.getConduta() != null) evolucao.setConduta(dto.getConduta());
        if (dto.getExamesRealizados() != null) evolucao.setExamesRealizados(dto.getExamesRealizados());
        if (dto.getPrescricao() != null) evolucao.setPrescricao(dto.getPrescricao());

        Evolucao updated = evolucaoRepository.save(evolucao);
        linhaDoTempoService.registrarEvolucao(updated);
        return EvolucaoDto.from(updated);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!evolucaoRepository.existsById(id)) {
            throw new IllegalArgumentException("Evolução não encontrada: " + id);
        }
        linhaDoTempoService.removerPorReferencia(id);
        evolucaoRepository.deleteById(id);
    }
}
