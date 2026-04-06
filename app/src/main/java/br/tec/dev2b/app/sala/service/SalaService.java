package br.tec.dev2b.app.sala.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.sala.dto.AtualizarSalaDto;
import br.tec.dev2b.app.sala.dto.CriarSalaDto;
import br.tec.dev2b.app.sala.dto.SalaDto;
import br.tec.dev2b.app.sala.model.Sala;
import br.tec.dev2b.app.sala.repository.SalaRepository;
import br.tec.dev2b.app.unidade.model.Unidade;
import br.tec.dev2b.app.unidade.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final EmpresaRepository empresaRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public SalaDto criar(CriarSalaDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Sala sala = Sala.builder()
                .nome(dto.getNome())
                .permitirOverbooking(dto.getPermitirOverbooking() != null ? dto.getPermitirOverbooking() : false)
                .empresa(empresa)
                .build();

        if (dto.getUnidadeId() != null) {
            Unidade unidade = unidadeRepository.findById(dto.getUnidadeId())
                    .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada: " + dto.getUnidadeId()));
            sala.setUnidade(unidade);
        }

        return SalaDto.from(salaRepository.save(sala));
    }

    @Transactional(readOnly = true)
    public List<SalaDto> listarPorEmpresa(UUID empresaId) {
        return salaRepository.findByEmpresaId(empresaId).stream()
                .map(SalaDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalaDto> listarAtivasPorEmpresa(UUID empresaId) {
        return salaRepository.findByEmpresaIdAndAtivaTrue(empresaId).stream()
                .map(SalaDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaDto buscarPorId(UUID id) {
        return salaRepository.findById(id)
                .map(SalaDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada: " + id));
    }

    @Transactional
    public SalaDto atualizar(UUID id, AtualizarSalaDto dto) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada: " + id));

        if (dto.getNome() != null) sala.setNome(dto.getNome());
        if (dto.getAtiva() != null) sala.setAtiva(dto.getAtiva());
        if (dto.getPermitirOverbooking() != null) sala.setPermitirOverbooking(dto.getPermitirOverbooking());
        
        if (dto.getUnidadeId() != null) {
            Unidade unidade = unidadeRepository.findById(dto.getUnidadeId())
                    .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada: " + dto.getUnidadeId()));
            sala.setUnidade(unidade);
        }

        return SalaDto.from(salaRepository.save(sala));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!salaRepository.existsById(id)) {
            throw new IllegalArgumentException("Sala não encontrada: " + id);
        }
        salaRepository.deleteById(id);
    }
}
