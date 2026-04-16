package br.tec.dev2b.app.integracao.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.integracao.dto.IntegracaoDto;
import br.tec.dev2b.app.integracao.dto.SalvarIntegracaoDto;
import br.tec.dev2b.app.integracao.model.Integracao;
import br.tec.dev2b.app.integracao.repository.IntegracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegracaoService {

    private final IntegracaoRepository integracaoRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<IntegracaoDto> listarPorEmpresa(UUID empresaId) {
        return integracaoRepository.findByEmpresaId(empresaId)
                .stream().map(IntegracaoDto::from).toList();
    }

    @Transactional(readOnly = true)
    public IntegracaoDto buscarPorTipo(UUID empresaId, String tipo) {
        return integracaoRepository.findByEmpresaIdAndTipo(empresaId, tipo)
                .map(IntegracaoDto::from)
                .orElse(null);
    }

    @Transactional
    public IntegracaoDto salvar(UUID empresaId, String tipo, SalvarIntegracaoDto dto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + empresaId));

        Integracao integracao = integracaoRepository
                .findByEmpresaIdAndTipo(empresaId, tipo)
                .orElseGet(() -> Integracao.builder()
                        .empresa(empresa)
                        .tipo(tipo)
                        .build());

        if (dto.getAtivo() != null) integracao.setAtivo(dto.getAtivo());
        if (dto.getConfiguracao() != null) integracao.setConfiguracao(dto.getConfiguracao());

        return IntegracaoDto.from(integracaoRepository.save(integracao));
    }

    @Transactional
    public void deletar(UUID empresaId, String tipo) {
        integracaoRepository.findByEmpresaIdAndTipo(empresaId, tipo)
                .ifPresent(integracaoRepository::delete);
    }
}
