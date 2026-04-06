package br.tec.dev2b.app.servico.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.servico.dto.AtualizarServicoDto;
import br.tec.dev2b.app.servico.dto.CriarServicoDto;
import br.tec.dev2b.app.servico.dto.ServicoDto;
import br.tec.dev2b.app.servico.model.Servico;
import br.tec.dev2b.app.servico.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public ServicoDto criar(CriarServicoDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Servico servico = Servico.builder()
                .nome(dto.getNome())
                .tipo(dto.getTipo() != null ? dto.getTipo() : "GERAL")
                .valor(dto.getValor())
                .empresa(empresa)
                .build();

        return ServicoDto.from(servicoRepository.save(servico));
    }

    @Transactional(readOnly = true)
    public List<ServicoDto> listarPorEmpresa(UUID empresaId) {
        return servicoRepository.findByEmpresaId(empresaId).stream()
                .map(ServicoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServicoDto> listarAtivosPorEmpresa(UUID empresaId) {
        return servicoRepository.findByEmpresaIdAndAtivoTrue(empresaId).stream()
                .map(ServicoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServicoDto> listarPorEmpresaETipo(UUID empresaId, String tipo) {
        return servicoRepository.findByEmpresaIdAndTipo(empresaId, tipo).stream()
                .map(ServicoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicoDto buscarPorId(UUID id) {
        return servicoRepository.findById(id)
                .map(ServicoDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));
    }

    @Transactional
    public ServicoDto atualizar(UUID id, AtualizarServicoDto dto) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));

        if (dto.getNome() != null) servico.setNome(dto.getNome());
        if (dto.getTipo() != null) servico.setTipo(dto.getTipo());
        if (dto.getValor() != null) servico.setValor(dto.getValor());
        if (dto.getAtivo() != null) servico.setAtivo(dto.getAtivo());

        return ServicoDto.from(servicoRepository.save(servico));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!servicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Serviço não encontrado: " + id);
        }
        servicoRepository.deleteById(id);
    }
}
