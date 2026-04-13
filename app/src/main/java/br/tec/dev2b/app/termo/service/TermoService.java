package br.tec.dev2b.app.termo.service;

import br.tec.dev2b.app.termo.dto.AceitarTermoDto;
import br.tec.dev2b.app.termo.dto.TermoDto;
import br.tec.dev2b.app.termo.model.Termo;
import br.tec.dev2b.app.termo.model.TermoAceite;
import br.tec.dev2b.app.termo.repository.TermoAceiteRepository;
import br.tec.dev2b.app.termo.repository.TermoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermoService {

    private final TermoRepository termoRepository;
    private final TermoAceiteRepository termoAceiteRepository;

    public TermoDto buscarTermoPorTipo(String tipo, UUID empresaId) {
        Termo termo = termoRepository.findByTipoAndAtivoTrue(tipo)
                .orElseThrow(() -> new IllegalArgumentException("Termo não encontrado"));

        boolean aceito = termoAceiteRepository.existsByTermoIdAndEmpresaId(termo.getId(), empresaId);

        return TermoDto.builder()
                .id(termo.getId())
                .titulo(termo.getTitulo())
                .conteudo(termo.getConteudo())
                .versao(termo.getVersao())
                .tipo(termo.getTipo())
                .ativo(termo.getAtivo())
                .createdAt(termo.getCreatedAt())
                .aceito(aceito)
                .build();
    }

    @Transactional
    public void aceitarTermo(AceitarTermoDto dto, HttpServletRequest request) {
        Termo termo = termoRepository.findById(dto.getTermoId())
                .orElseThrow(() -> new IllegalArgumentException("Termo não encontrado"));

        // Verificar se já foi aceito
        if (termoAceiteRepository.existsByTermoIdAndEmpresaId(dto.getTermoId(), dto.getEmpresaId())) {
            log.info("Termo já aceito pela empresa: {}", dto.getEmpresaId());
            return;
        }

        String ipAddress = getClientIp(request);

        TermoAceite aceite = TermoAceite.builder()
                .termo(termo)
                .empresaId(dto.getEmpresaId())
                .usuarioId(dto.getUsuarioId())
                .ipAceite(ipAddress)
                .build();

        termoAceiteRepository.save(aceite);
        log.info("Termo {} aceito pela empresa {} (usuário: {})", termo.getTitulo(), dto.getEmpresaId(), dto.getUsuarioId());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
