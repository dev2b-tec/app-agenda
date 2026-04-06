package br.tec.dev2b.app.cnae.service;

import br.tec.dev2b.app.cnae.dto.AtualizarCnaeDto;
import br.tec.dev2b.app.cnae.dto.CnaeDto;
import br.tec.dev2b.app.cnae.dto.CriarCnaeDto;
import br.tec.dev2b.app.cnae.model.Cnae;
import br.tec.dev2b.app.cnae.repository.CnaeRepository;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CnaeService {

    private final CnaeRepository cnaeRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public CnaeDto criar(CriarCnaeDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + dto.getEmpresaId()));

        Cnae cnae = Cnae.builder()
                .empresa(empresa)
                .codigoCnae(dto.getCodigoCnae())
                .tipoTributacao(dto.getTipoTributacao())
                .discriminacaoServicos(dto.getDiscriminacaoServicos())
                .codigoFederal(dto.getCodigoFederal())
                .codigoMunicipal(dto.getCodigoMunicipal())
                .issRetido(dto.getIssRetido() != null ? dto.getIssRetido() : false)
                .irRetido(dto.getIrRetido() != null ? dto.getIrRetido() : false)
                .inssRetido(dto.getInssRetido() != null ? dto.getInssRetido() : false)
                .csllRetido(dto.getCsllRetido() != null ? dto.getCsllRetido() : false)
                .pisRetido(dto.getPisRetido() != null ? dto.getPisRetido() : false)
                .cofinsRetido(dto.getCofinsRetido() != null ? dto.getCofinsRetido() : false)
                .aliquotaIss(dto.getAliquotaIss())
                .aliquotaInss(dto.getAliquotaInss())
                .aliquotaIr(dto.getAliquotaIr())
                .aliquotaCsll(dto.getAliquotaCsll())
                .aliquotaPis(dto.getAliquotaPis())
                .aliquotaCofins(dto.getAliquotaCofins())
                .padrao(dto.getPadrao() != null ? dto.getPadrao() : false)
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .build();

        return CnaeDto.from(cnaeRepository.save(cnae));
    }

    @Transactional(readOnly = true)
    public List<CnaeDto> listarPorEmpresa(UUID empresaId) {
        return cnaeRepository.findByEmpresaIdOrderByCodigoCnaeAsc(empresaId).stream()
                .map(CnaeDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CnaeDto> listarAtivosPorEmpresa(UUID empresaId) {
        return cnaeRepository.findByEmpresaIdAndAtivoTrueOrderByCodigoCnaeAsc(empresaId).stream()
                .map(CnaeDto::from)
                .toList();
    }

    @Transactional
    public CnaeDto atualizar(UUID id, AtualizarCnaeDto dto) {
        Cnae cnae = cnaeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CNAE não encontrado: " + id));

        if (dto.getCodigoCnae() != null)            cnae.setCodigoCnae(dto.getCodigoCnae());
        if (dto.getTipoTributacao() != null)         cnae.setTipoTributacao(dto.getTipoTributacao());
        if (dto.getDiscriminacaoServicos() != null)  cnae.setDiscriminacaoServicos(dto.getDiscriminacaoServicos());
        if (dto.getCodigoFederal() != null)          cnae.setCodigoFederal(dto.getCodigoFederal());
        if (dto.getCodigoMunicipal() != null)        cnae.setCodigoMunicipal(dto.getCodigoMunicipal());
        if (dto.getIssRetido() != null)              cnae.setIssRetido(dto.getIssRetido());
        if (dto.getIrRetido() != null)               cnae.setIrRetido(dto.getIrRetido());
        if (dto.getInssRetido() != null)             cnae.setInssRetido(dto.getInssRetido());
        if (dto.getCsllRetido() != null)             cnae.setCsllRetido(dto.getCsllRetido());
        if (dto.getPisRetido() != null)              cnae.setPisRetido(dto.getPisRetido());
        if (dto.getCofinsRetido() != null)           cnae.setCofinsRetido(dto.getCofinsRetido());
        if (dto.getAliquotaIss() != null)            cnae.setAliquotaIss(dto.getAliquotaIss());
        if (dto.getAliquotaInss() != null)           cnae.setAliquotaInss(dto.getAliquotaInss());
        if (dto.getAliquotaIr() != null)             cnae.setAliquotaIr(dto.getAliquotaIr());
        if (dto.getAliquotaCsll() != null)           cnae.setAliquotaCsll(dto.getAliquotaCsll());
        if (dto.getAliquotaPis() != null)            cnae.setAliquotaPis(dto.getAliquotaPis());
        if (dto.getAliquotaCofins() != null)         cnae.setAliquotaCofins(dto.getAliquotaCofins());
        if (dto.getPadrao() != null)                 cnae.setPadrao(dto.getPadrao());
        if (dto.getAtivo() != null)                  cnae.setAtivo(dto.getAtivo());

        return CnaeDto.from(cnaeRepository.save(cnae));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!cnaeRepository.existsById(id)) {
            throw new IllegalArgumentException("CNAE não encontrado: " + id);
        }
        cnaeRepository.deleteById(id);
    }
}
