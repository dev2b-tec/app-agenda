package br.tec.dev2b.app.infra.cnpj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ReceitaWsClient {

    private static final String RECEITA_WS_URL = "https://www.receitaws.com.br/v1/cnpj/";
    private final RestTemplate restTemplate = new RestTemplate();

    public CnpjResponse consultarCnpj(String cnpj) {
        try {
            String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
            
            if (cnpjLimpo.length() != 14) {
                throw new IllegalArgumentException("CNPJ deve conter 14 dígitos");
            }

            String url = RECEITA_WS_URL + cnpjLimpo;
            CnpjResponse response = restTemplate.getForObject(url, CnpjResponse.class);
            
            if (response != null && "ERROR".equals(response.getStatus())) {
                throw new IllegalArgumentException(response.getMessage() != null ? response.getMessage() : "CNPJ inválido");
            }
            
            return response;
        } catch (Exception e) {
            log.error("Erro ao consultar CNPJ na Receita Federal: {}", e.getMessage());
            throw new IllegalArgumentException("Erro ao validar CNPJ: " + e.getMessage());
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CnpjResponse {
        private String status;
        private String message;
        
        @JsonProperty("nome")
        private String razaoSocial;
        
        @JsonProperty("fantasia")
        private String nomeFantasia;
        
        private String cnpj;
        
        @JsonProperty("abertura")
        private String dataAbertura;
        
        private String situacao;
        
        @JsonProperty("uf")
        private String uf;
        
        @JsonProperty("municipio")
        private String municipio;
        
        @JsonProperty("logradouro")
        private String logradouro;
        
        @JsonProperty("numero")
        private String numero;
        
        @JsonProperty("complemento")
        private String complemento;
        
        @JsonProperty("bairro")
        private String bairro;
        
        @JsonProperty("cep")
        private String cep;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("telefone")
        private String telefone;
    }
}
