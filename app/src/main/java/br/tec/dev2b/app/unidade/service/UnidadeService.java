package br.tec.dev2b.app.unidade.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.unidade.dto.AtualizarUnidadeDto;
import br.tec.dev2b.app.unidade.dto.CriarUnidadeDto;
import br.tec.dev2b.app.unidade.dto.UnidadeDto;
import br.tec.dev2b.app.unidade.model.Unidade;
import br.tec.dev2b.app.unidade.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public UnidadeDto criar(CriarUnidadeDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Unidade unidade = Unidade.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .empresa(empresa)
                .build();

        return UnidadeDto.from(unidadeRepository.save(unidade));
    }

    @Transactional(readOnly = true)
    public List<UnidadeDto> listarPorEmpresa(UUID empresaId) {
        return unidadeRepository.findByEmpresaId(empresaId).stream()
                .map(UnidadeDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UnidadeDto> listarAtivasPorEmpresa(UUID empresaId) {
        return unidadeRepository.findByEmpresaIdAndAtivaTrue(empresaId).stream()
                .map(UnidadeDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnidadeDto buscarPorId(UUID id) {
        return unidadeRepository.findById(id)
                .map(UnidadeDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada: " + id));
    }

    @Transactional
    public UnidadeDto atualizar(UUID id, AtualizarUnidadeDto dto) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada: " + id));

        if (dto.getNome() != null) unidade.setNome(dto.getNome());
        if (dto.getDescricao() != null) unidade.setDescricao(dto.getDescricao());
        if (dto.getAtiva() != null) unidade.setAtiva(dto.getAtiva());

        return UnidadeDto.from(unidadeRepository.save(unidade));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!unidadeRepository.existsById(id)) {
            throw new IllegalArgumentException("Unidade não encontrada: " + id);
        }
        unidadeRepository.deleteById(id);
    }
}
