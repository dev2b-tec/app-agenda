package br.tec.dev2b.app.linhadotempo.service;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import br.tec.dev2b.app.anamnese.model.RespostaAnamnese;
import br.tec.dev2b.app.documento.model.Documento;
import br.tec.dev2b.app.evolucao.model.Evolucao;
import br.tec.dev2b.app.linhadotempo.dto.LinhaDoTempoDto;
import br.tec.dev2b.app.linhadotempo.model.LinhaDoTempo;
import br.tec.dev2b.app.linhadotempo.repository.LinhaDoTempoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinhaDoTempoService {

    private final LinhaDoTempoRepository linhaDoTempoRepository;

    @Transactional
    public void registrarEvolucao(Evolucao evolucao) {
        Optional<LinhaDoTempo> existente = linhaDoTempoRepository.findByReferenciaId(evolucao.getId());

        if (existente.isPresent()) {
            LinhaDoTempo entry = existente.get();
            entry.setTitulo(evolucao.getTitulo() != null ? evolucao.getTitulo() : "(sem título)");
            entry.setProfissional(evolucao.getProfissional());
            entry.setData(evolucao.getData());
            entry.setAssinado(evolucao.getAssinado());
            linhaDoTempoRepository.save(entry);
        } else {
            LinhaDoTempo entry = LinhaDoTempo.builder()
                    .paciente(evolucao.getPaciente())
                    .tipo("EVOLUCAO")
                    .titulo(evolucao.getTitulo() != null ? evolucao.getTitulo() : "(sem título)")
                    .profissional(evolucao.getProfissional())
                    .referenciaId(evolucao.getId())
                    .data(evolucao.getData())
                    .assinado(evolucao.getAssinado())
                    .build();
            linhaDoTempoRepository.save(entry);
        }
    }

    @Transactional
    public void registrarAgendamento(Agendamento agendamento) {
        if (agendamento.getPaciente() == null) return;
        Optional<LinhaDoTempo> existente = linhaDoTempoRepository.findByReferenciaId(agendamento.getId());

        String titulo     = agendamento.getStatus() != null ? agendamento.getStatus() : "Agendamento";
        String profissional = agendamento.getUsuarioNome();
        LocalDate data    = agendamento.getInicio().toLocalDate();
        LocalTime hora    = agendamento.getInicio().toLocalTime();

        if (existente.isPresent()) {
            LinhaDoTempo entry = existente.get();
            entry.setTitulo(titulo);
            entry.setProfissional(profissional);
            entry.setData(data);
            entry.setHora(hora);
            linhaDoTempoRepository.save(entry);
        } else {
            LinhaDoTempo entry = LinhaDoTempo.builder()
                    .paciente(agendamento.getPaciente())
                    .tipo("AGENDAMENTO")
                    .titulo(titulo)
                    .profissional(profissional)
                    .referenciaId(agendamento.getId())
                    .data(data)
                    .hora(hora)
                    .assinado(true)
                    .build();
            linhaDoTempoRepository.save(entry);
        }
    }

    @Transactional
    public void removerPorReferencia(UUID referenciaId) {
        linhaDoTempoRepository.deleteByReferenciaId(referenciaId);
    }

    @Transactional
    public void registrarAnamnese(RespostaAnamnese resposta, String tituloAnamnese) {
        Optional<LinhaDoTempo> existente = linhaDoTempoRepository.findByReferenciaId(resposta.getId());

        if (existente.isPresent()) {
            LinhaDoTempo entry = existente.get();
            entry.setProfissional(resposta.getProfissional());
            entry.setData(resposta.getData());
            linhaDoTempoRepository.save(entry);
        } else {
            LinhaDoTempo entry = LinhaDoTempo.builder()
                    .paciente(resposta.getPaciente())
                    .tipo("ANAMNESE")
                    .titulo(tituloAnamnese)
                    .profissional(resposta.getProfissional())
                    .referenciaId(resposta.getId())
                    .data(resposta.getData())
                    .assinado(false)
                    .build();
            linhaDoTempoRepository.save(entry);
        }
    }

    @Transactional
    public void registrarDocumento(Documento documento) {
        Optional<LinhaDoTempo> existente = linhaDoTempoRepository.findByReferenciaId(documento.getId());
        String profissionalNome = documento.getUsuario() != null ? documento.getUsuario().getNome() : null;

        if (existente.isPresent()) {
            LinhaDoTempo entry = existente.get();
            entry.setTitulo(documento.getTitulo());
            entry.setProfissional(profissionalNome);
            linhaDoTempoRepository.save(entry);
        } else {
            LinhaDoTempo entry = LinhaDoTempo.builder()
                    .paciente(documento.getPaciente())
                    .tipo("DOCUMENTO")
                    .titulo(documento.getTitulo())
                    .profissional(profissionalNome)
                    .referenciaId(documento.getId())
                    .data(documento.getCreatedAt().toLocalDate())
                    .assinado(false)
                    .build();
            linhaDoTempoRepository.save(entry);
        }
    }

    @Transactional(readOnly = true)
    public List<LinhaDoTempoDto> listarPorPaciente(UUID pacienteId) {
        return linhaDoTempoRepository.findByPacienteIdOrderByDataDescCreatedAtDesc(pacienteId)
                .stream()
                .map(LinhaDoTempoDto::from)
                .toList();
    }
}
