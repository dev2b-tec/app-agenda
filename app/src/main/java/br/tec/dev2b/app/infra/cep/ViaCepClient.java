package br.tec.dev2b.app.infra.cep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ViaCepClient {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/%s/json/";
    private final RestTemplate restTemplate = new RestTemplate();

    public ViaCepDto consultar(String cep) {
        try {
            String url = String.format(VIA_CEP_URL, cep);
            ViaCepDto response = restTemplate.getForObject(url, ViaCepDto.class);
            if (response == null || Boolean.TRUE.equals(response.getErro())) {
                throw new IllegalArgumentException("CEP não encontrado: " + cep);
            }
            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao consultar ViaCEP para o CEP {}: {}", cep, e.getMessage());
            throw new IllegalArgumentException("Erro ao consultar CEP: " + e.getMessage());
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ViaCepDto {

        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;

        @JsonProperty("localidade")
        private String cidade;

        private String uf;
        private String ibge;
        private String ddd;

        @JsonProperty("erro")
        private Boolean erro;
    }
}
