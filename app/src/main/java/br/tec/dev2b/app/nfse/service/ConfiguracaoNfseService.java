package br.tec.dev2b.app.nfse.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.infra.cnpj.ReceitaWsClient;
import br.tec.dev2b.app.nfse.dto.ConfiguracaoNfseDto;
import br.tec.dev2b.app.nfse.dto.ConsultarCnpjDto;
import br.tec.dev2b.app.nfse.model.ConfiguracaoNfse;
import br.tec.dev2b.app.nfse.repository.ConfiguracaoNfseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfiguracaoNfseService {

    private final ConfiguracaoNfseRepository configuracaoNfseRepository;
    private final EmpresaRepository empresaRepository;
    private final ReceitaWsClient receitaWsClient;

    @Transactional(readOnly = true)
    public ConfiguracaoNfseDto buscarPorEmpresa(UUID empresaId) {
        return configuracaoNfseRepository.findByEmpresaId(empresaId)
                .map(ConfiguracaoNfseDto::from)
                .orElse(null);
    }

    @Transactional
    public ConfiguracaoNfseDto consultarEValidarCnpj(ConsultarCnpjDto dto) {
        log.info("Consultando CNPJ na Receita Federal: {}", dto.getCnpj());
        
        // Valida CNPJ na Receita Federal
        ReceitaWsClient.CnpjResponse response = receitaWsClient.consultarCnpj(dto.getCnpj());
        
        if (response == null) {
            throw new IllegalArgumentException("CNPJ não encontrado na Receita Federal");
        }

        // Verifica se empresa existe
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        // Busca ou cria configuração
        ConfiguracaoNfse config = configuracaoNfseRepository.findByEmpresaId(dto.getEmpresaId())
                .orElse(ConfiguracaoNfse.builder()
                        .empresa(empresa)
                        .build());

        // Atualiza com dados da Receita Federal
        config.setCnpj(response.getCnpj());
        config.setRazaoSocial(response.getRazaoSocial());
        config.setNomeFantasia(response.getNomeFantasia());
        config.setDataAbertura(response.getDataAbertura());
        config.setSituacao(response.getSituacao());
        config.setUf(response.getUf());
        config.setMunicipio(response.getMunicipio());
        config.setLogradouro(response.getLogradouro());
        config.setNumero(response.getNumero());
        config.setComplemento(response.getComplemento());
        config.setBairro(response.getBairro());
        config.setCep(response.getCep());
        config.setEmail(response.getEmail());
        config.setTelefone(response.getTelefone());
        config.setValidadoReceita(true);

        ConfiguracaoNfse saved = configuracaoNfseRepository.save(config);
        log.info("CNPJ validado e salvo com sucesso: {}", dto.getCnpj());
        
        return ConfiguracaoNfseDto.from(saved);
    }

    @Transactional
    public void deletar(UUID empresaId) {
        configuracaoNfseRepository.findByEmpresaId(empresaId)
                .ifPresent(configuracaoNfseRepository::delete);
    }
}
