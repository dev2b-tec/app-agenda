package br.tec.dev2b.app.assinatura.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssinaturaIntegrationService {

    @Value("${pagamento.service.url:http://app-integration-mercado-pago:8013}")
    private String pagamentoServiceUrl;

    private final RestTemplate restTemplate;

    public List<Map<String, Object>> listarPlanos() {
        try {
            String url = pagamentoServiceUrl + "/api/v1/assinaturas/planos";
            ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {}
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erro ao listar planos: {}", e.getMessage(), e);
            throw new RuntimeException("Serviço de pagamento indisponível", e);
        }
    }

    public Map<String, Object> criarAssinatura(Map<String, Object> dto) {
        try {
            String url = pagamentoServiceUrl + "/api/v1/assinaturas";
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(dto),
                    new ParameterizedTypeReference<>() {}
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erro ao criar assinatura: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar assinatura: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> criarPagamentoPix(Map<String, Object> dto) {
        try {
            String url = pagamentoServiceUrl + "/api/v1/assinaturas/pix";
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(dto),
                    new ParameterizedTypeReference<>() {}
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erro ao criar pagamento PIX: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar PIX: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> listarAssinaturasPorEmpresa(String empresaId) {
        try {
            String url = pagamentoServiceUrl + "/api/v1/assinaturas/empresa/" + empresaId;
            ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {}
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erro ao listar assinaturas da empresa {}: {}", empresaId, e.getMessage(), e);
            throw new RuntimeException("Erro ao listar assinaturas", e);
        }
    }

    public Map<String, Object> cancelarAssinatura(String id) {
        try {
            String url = pagamentoServiceUrl + "/api/v1/assinaturas/" + id;
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    url, HttpMethod.DELETE, null,
                    new ParameterizedTypeReference<>() {}
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erro ao cancelar assinatura {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao cancelar assinatura", e);
        }
    }

    public Map<String, Object> buscarConfigMp() {
        try {
            String url = pagamentoServiceUrl + "/api/v1/config/mp";
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {}
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("Erro ao buscar config MP: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar config de pagamento", e);
        }
    }
}
