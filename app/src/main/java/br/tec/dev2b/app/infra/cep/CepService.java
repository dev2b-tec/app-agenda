package br.tec.dev2b.app.infra.cep;

import br.tec.dev2b.app.infra.cep.ViaCepClient.ViaCepDto;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CepService {

    private static final String CACHE_PREFIX = "cep:";
    private static final long CACHE_TTL_DIAS = 30;

    private final ViaCepClient viaCepClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ViaCepDto consultar(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve conter 8 dígitos");
        }

        String chave = CACHE_PREFIX + cepLimpo;

        // Tenta o cache primeiro
        Object cached = redisTemplate.opsForValue().get(chave);
        if (cached != null) {
            log.debug("CEP {} encontrado no cache", cepLimpo);
            return objectMapper.convertValue(cached, ViaCepDto.class);
        }

        // Não estava no cache — consulta o ViaCEP
        log.debug("CEP {} não encontrado no cache, consultando ViaCEP", cepLimpo);
        ViaCepDto dto = viaCepClient.consultar(cepLimpo);

        // Armazena no cache com TTL de 30 dias
        redisTemplate.opsForValue().set(chave, dto, CACHE_TTL_DIAS, TimeUnit.DAYS);

        return dto;
    }
}
