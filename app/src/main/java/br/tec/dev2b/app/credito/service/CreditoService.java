package br.tec.dev2b.app.credito.service;

import br.tec.dev2b.app.credito.dto.*;
import br.tec.dev2b.app.credito.model.DadosPagamento;
import br.tec.dev2b.app.credito.model.HistoricoCredito;
import br.tec.dev2b.app.credito.repository.DadosPagamentoRepository;
import br.tec.dev2b.app.credito.repository.HistoricoCreditoRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditoService {

    private final HistoricoCreditoRepository historicoCreditoRepository;
    private final DadosPagamentoRepository dadosPagamentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public HistoricoCreditoDto comprarCreditos(ComprarCreditosDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + dto.getUsuarioId()));

        DadosPagamento dadosPagamento = criarOuAtualizarDadosPagamento(dto.getDadosPagamento(), usuario);

        HistoricoCredito historico = HistoricoCredito.builder()
                .usuario(usuario)
                .dadosPagamento(dadosPagamento)
                .quantidade(dto.getQuantidade())
                .valorPago(dto.getValorPago())
                .status("CONFIRMADO")
                .build();

        return HistoricoCreditoDto.from(historicoCreditoRepository.save(historico));
    }

    private DadosPagamento criarOuAtualizarDadosPagamento(CriarDadosPagamentoDto dto, Usuario usuario) {
        DadosPagamento dados = DadosPagamento.builder()
                .usuario(usuario)
                .nomeCompleto(dto.getNomeCompleto())
                .email(dto.getEmail())
                .pais(dto.getPais() != null ? dto.getPais() : "BRA")
                .numeroTelefone(dto.getNumeroTelefone())
                .tipoDocumento(dto.getTipoDocumento())
                .numeroDocumento(dto.getNumeroDocumento())
                .logradouro(dto.getLogradouro())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cep(dto.getCep())
                .metodoPagamento(dto.getMetodoPagamento())
                .nomeCartao(dto.getNomeCartao())
                .numeroCartao(dto.getNumeroCartao())
                .cvv(dto.getCvv())
                .expiracao(dto.getExpiracao())
                .build();

        return dadosPagamentoRepository.save(dados);
    }

    @Transactional(readOnly = true)
    public List<HistoricoCreditoDto> listarHistorico(UUID usuarioId) {
        return historicoCreditoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId).stream()
                .map(HistoricoCreditoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Integer calcularSaldo(UUID usuarioId) {
        return historicoCreditoRepository.calcularSaldoCreditos(usuarioId);
    }

    @Transactional(readOnly = true)
    public DadosPagamentoDto obterUltimosDadosPagamento(UUID usuarioId) {
        return dadosPagamentoRepository.findFirstByUsuarioIdOrderByCreatedAtDesc(usuarioId)
                .map(DadosPagamentoDto::from)
                .orElse(null);
    }
}
