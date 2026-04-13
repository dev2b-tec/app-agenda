package br.tec.dev2b.app.ia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IAIntegrationService {

    @Value("${ia.service.url:http://localhost:8013}")
    private String iaServiceUrl;

    private final RestTemplate restTemplate;
    private final PromptsEspecialidadeService promptsService;

    /**
     * Gera resumo automático de evolução clínica usando IA
     */
    public String gerarResumoEvolucao(String comentariosGerais, String conduta, 
                                      String examesRealizados, String prescricao,
                                      UUID empresaId, UUID usuarioId) {
        log.info("Gerando resumo de evolução com IA para empresa: {}", empresaId);

        try {
            // Montar contexto da evolução
            StringBuilder contexto = new StringBuilder();
            if (comentariosGerais != null && !comentariosGerais.trim().isEmpty()) {
                contexto.append("Comentários Gerais:\n").append(comentariosGerais).append("\n\n");
            }
            if (conduta != null && !conduta.trim().isEmpty()) {
                contexto.append("Conduta:\n").append(conduta).append("\n\n");
            }
            if (examesRealizados != null && !examesRealizados.trim().isEmpty()) {
                contexto.append("Exames Realizados:\n").append(examesRealizados).append("\n\n");
            }
            if (prescricao != null && !prescricao.trim().isEmpty()) {
                contexto.append("Prescrição:\n").append(prescricao).append("\n\n");
            }

            if (contexto.length() == 0) {
                return null;
            }

            // Criar mensagens para o chat
            List<Map<String, String>> mensagens = new ArrayList<>();
            mensagens.add(Map.of(
                "role", "system",
                "content", "Você é um assistente médico especializado em criar resumos concisos de evoluções clínicas. " +
                          "IMPORTANTE: Escreva em texto corrido, de forma natural e amigável, SEM usar asteriscos, marcadores ou formatação especial. " +
                          "Use apenas texto simples e fluido. Seja breve e objetivo, destacando apenas os pontos mais relevantes do atendimento."
            ));
            mensagens.add(Map.of(
                "role", "user",
                "content", "Resuma de forma concisa e em texto corrido (sem asteriscos ou formatação) a seguinte evolução clínica:\n\n" + contexto.toString()
            ));

            // Criar request para IA
            Map<String, Object> request = Map.of(
                "empresaId", empresaId.toString(),
                "usuarioId", usuarioId != null ? usuarioId.toString() : "",
                "modelo", "gpt-4o",
                "temperatura", 0.3,
                "maxTokens", 500,
                "mensagens", mensagens,
                "salvarHistorico", false
            );

            // Chamar serviço de IA
            String url = iaServiceUrl + "/api/v1/conversas/chat/completions";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("content")) {
                String resumo = (String) response.get("content");
                log.info("Resumo gerado com sucesso - {} tokens usados", response.get("tokensTotal"));
                return resumo;
            }

            return null;
        } catch (Exception e) {
            log.error("Erro ao gerar resumo com IA: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gera análise de anamnese usando IA
     */
    public String analisarAnamnese(String respostasTexto, UUID empresaId, UUID usuarioId) {
        log.info("Analisando anamnese com IA para empresa: {}", empresaId);

        try {
            List<Map<String, String>> mensagens = new ArrayList<>();
            mensagens.add(Map.of(
                "role", "system",
                "content", "Você é um assistente médico especializado em análise de anamneses. " +
                          "Identifique pontos de atenção, possíveis fatores de risco e sugestões de investigação."
            ));
            mensagens.add(Map.of(
                "role", "user",
                "content", "Analise a seguinte anamnese e forneça insights clínicos relevantes:\n\n" + respostasTexto
            ));

            Map<String, Object> request = Map.of(
                "empresaId", empresaId.toString(),
                "usuarioId", usuarioId != null ? usuarioId.toString() : "",
                "modelo", "gpt-4o",
                "temperatura", 0.5,
                "maxTokens", 800,
                "mensagens", mensagens,
                "salvarHistorico", false
            );

            String url = iaServiceUrl + "/api/v1/conversas/chat/completions";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("content")) {
                return (String) response.get("content");
            }

            return null;
        } catch (Exception e) {
            log.error("Erro ao analisar anamnese com IA: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gera sugestão de documento usando IA
     */
    public String gerarSugestaoDocumento(String tipo, String contexto, UUID empresaId, UUID usuarioId) {
        log.info("Gerando sugestão de documento tipo '{}' com IA", tipo);

        try {
            String promptPorTipo = switch (tipo.toLowerCase()) {
                case "atestado" -> "Gere um modelo de atestado médico profissional baseado no contexto fornecido.";
                case "receita" -> "Gere um modelo de receita médica profissional baseado no contexto fornecido.";
                case "relatorio" -> "Gere um modelo de relatório médico profissional baseado no contexto fornecido.";
                default -> "Gere um documento médico profissional baseado no contexto fornecido.";
            };

            List<Map<String, String>> mensagens = new ArrayList<>();
            mensagens.add(Map.of(
                "role", "system",
                "content", "Você é um assistente médico especializado em documentação clínica. " +
                          "Gere documentos profissionais, claros e de acordo com as normas médicas."
            ));
            mensagens.add(Map.of(
                "role", "user",
                "content", promptPorTipo + "\n\nContexto:\n" + contexto
            ));

            Map<String, Object> request = Map.of(
                "empresaId", empresaId.toString(),
                "usuarioId", usuarioId != null ? usuarioId.toString() : "",
                "modelo", "gpt-4o",
                "temperatura", 0.4,
                "maxTokens", 1000,
                "mensagens", mensagens,
                "salvarHistorico", false
            );

            String url = iaServiceUrl + "/api/v1/conversas/chat/completions";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("content")) {
                return (String) response.get("content");
            }

            return null;
        } catch (Exception e) {
            log.error("Erro ao gerar sugestão de documento com IA: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Processa transcrição de áudio de relato do paciente
     */
    public String processarRelato(String transcricao, String especialidade, UUID empresaId, UUID usuarioId) {
        log.info("Processando relato com IA - Especialidade: {}", especialidade);

        try {
            String promptSistema = promptsService.getPromptRelato(especialidade);

            List<Map<String, String>> mensagens = new ArrayList<>();
            mensagens.add(Map.of(
                "role", "system",
                "content", promptSistema
            ));
            mensagens.add(Map.of(
                "role", "user",
                "content", "Organize o seguinte relato do paciente:\n\n" + transcricao
            ));

            Map<String, Object> request = Map.of(
                "empresaId", empresaId.toString(),
                "usuarioId", usuarioId != null ? usuarioId.toString() : "",
                "modelo", "gpt-4o",
                "temperatura", 0.4,
                "maxTokens", 800,
                "mensagens", mensagens,
                "salvarHistorico", false
            );

            String url = iaServiceUrl + "/api/v1/conversas/chat/completions";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("content")) {
                String resultado = (String) response.get("content");
                log.info("Relato processado com sucesso - {} tokens usados", response.get("tokensTotal"));
                return resultado;
            }

            return null;
        } catch (Exception e) {
            log.error("Erro ao processar relato com IA: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Processa transcrição de áudio de atendimento
     */
    public String processarAtendimento(String transcricao, String especialidade, UUID empresaId, UUID usuarioId) {
        log.info("Processando atendimento com IA - Especialidade: {}", especialidade);

        try {
            String promptSistema = promptsService.getPromptAtendimento(especialidade);

            List<Map<String, String>> mensagens = new ArrayList<>();
            mensagens.add(Map.of(
                "role", "system",
                "content", promptSistema
            ));
            mensagens.add(Map.of(
                "role", "user",
                "content", "Organize as seguintes anotações do atendimento:\n\n" + transcricao
            ));

            Map<String, Object> request = Map.of(
                "empresaId", empresaId.toString(),
                "usuarioId", usuarioId != null ? usuarioId.toString() : "",
                "modelo", "gpt-4o",
                "temperatura", 0.4,
                "maxTokens", 1000,
                "mensagens", mensagens,
                "salvarHistorico", false
            );

            String url = iaServiceUrl + "/api/v1/conversas/chat/completions";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("content")) {
                String resultado = (String) response.get("content");
                log.info("Atendimento processado com sucesso - {} tokens usados", response.get("tokensTotal"));
                return resultado;
            }

            return null;
        } catch (Exception e) {
            log.error("Erro ao processar atendimento com IA: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gera sugestão para comentários gerais baseado no texto atual
     */
    public String gerarSugestaoComentarios(String textoAtual, String especialidade, UUID empresaId, UUID usuarioId) {
        log.info("Gerando sugestão para comentários - Especialidade: {}", especialidade);

        try {
            String promptSistema = "Você é um assistente de " + especialidade.toLowerCase() + ". " +
                "Analise o texto fornecido e sugira melhorias, complementações ou organize melhor as informações. " +
                "Mantenha o conteúdo original mas torne-o mais claro e profissional. " +
                "Use texto corrido, sem asteriscos ou formatação especial.";

            List<Map<String, String>> mensagens = new ArrayList<>();
            mensagens.add(Map.of(
                "role", "system",
                "content", promptSistema
            ));
            mensagens.add(Map.of(
                "role", "user",
                "content", "Melhore e organize o seguinte texto:\n\n" + textoAtual
            ));

            Map<String, Object> request = Map.of(
                "empresaId", empresaId.toString(),
                "usuarioId", usuarioId != null ? usuarioId.toString() : "",
                "modelo", "gpt-4o",
                "temperatura", 0.5,
                "maxTokens", 800,
                "mensagens", mensagens,
                "salvarHistorico", false
            );

            String url = iaServiceUrl + "/api/v1/conversas/chat/completions";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("content")) {
                String resultado = (String) response.get("content");
                log.info("Sugestão gerada com sucesso - {} tokens usados", response.get("tokensTotal"));
                return resultado;
            }

            return null;
        } catch (Exception e) {
            log.error("Erro ao gerar sugestão com IA: {}", e.getMessage(), e);
            return null;
        }
    }
    /**
     * Transcreve audio via app-integration-ia (Whisper)
     */
    @SuppressWarnings("unchecked")
    public String transcreverAudio(MultipartFile audio) {
        log.info("Transcrevendo \u00e1udio: {} ({} bytes)", audio.getOriginalFilename(), audio.getSize());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            byte[] bytes = audio.getBytes();
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return Objects.requireNonNullElse(audio.getOriginalFilename(), "audio.webm");
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String url = iaServiceUrl + "/api/v1/transcricao/audio";

            Map<String, String> response = restTemplate.postForObject(url, requestEntity, Map.class);
            if (response != null && response.containsKey("transcricao")) {
                log.info("Transcri\u00e7\u00e3o conclu\u00edda: {} caracteres", response.get("transcricao").length());
                return response.get("transcricao");
            }
            return null;
        } catch (Exception e) {
            log.error("Erro ao transcrever \u00e1udio: {}", e.getMessage(), e);
            return null;
        }
    }}
