package br.tec.dev2b.app.marcador.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.marcador.dto.AtualizarMarcadorDto;
import br.tec.dev2b.app.marcador.dto.CriarMarcadorDto;
import br.tec.dev2b.app.marcador.dto.MarcadorDto;
import br.tec.dev2b.app.marcador.model.Marcador;
import br.tec.dev2b.app.marcador.repository.MarcadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarcadorService {

    private final MarcadorRepository marcadorRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public MarcadorDto criar(CriarMarcadorDto dto) {
        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.empresaId()));

        if (marcadorRepository.existsByEmpresaIdAndTipo(dto.empresaId(), dto.tipo())) {
            throw new IllegalArgumentException("Já existe um marcador com o tipo '" + dto.tipo() + "' para esta empresa.");
        }

        Marcador marcador = Marcador.builder()
                .tipo(dto.tipo())
                .cor(dto.cor())
                .empresa(empresa)
                .build();

        return MarcadorDto.from(marcadorRepository.save(marcador));
    }

    @Transactional(readOnly = true)
    public List<MarcadorDto> listarPorEmpresa(UUID empresaId) {
        return marcadorRepository.findByEmpresaId(empresaId).stream()
                .map(MarcadorDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MarcadorDto buscarPorId(UUID id) {
        return marcadorRepository.findById(id)
                .map(MarcadorDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Marcador não encontrado: " + id));
    }

    @Transactional
    public MarcadorDto atualizar(UUID id, AtualizarMarcadorDto dto) {
        Marcador marcador = marcadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marcador não encontrado: " + id));

        if (dto.tipo() != null && !dto.tipo().equals(marcador.getTipo())) {
            if (marcadorRepository.existsByEmpresaIdAndTipo(marcador.getEmpresa().getId(), dto.tipo())) {
                throw new IllegalArgumentException("Já existe um marcador com o tipo '" + dto.tipo() + "' para esta empresa.");
            }
            marcador.setTipo(dto.tipo());
        }

        if (dto.cor() != null) {
            marcador.setCor(dto.cor());
        }

        return MarcadorDto.from(marcadorRepository.save(marcador));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!marcadorRepository.existsById(id)) {
            throw new IllegalArgumentException("Marcador não encontrado: " + id);
        }
        marcadorRepository.deleteById(id);
    }
}
