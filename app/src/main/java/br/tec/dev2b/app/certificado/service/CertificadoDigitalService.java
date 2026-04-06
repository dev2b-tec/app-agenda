package br.tec.dev2b.app.certificado.service;

import br.tec.dev2b.app.certificado.dto.CertificadoDigitalDto;
import br.tec.dev2b.app.certificado.model.CertificadoDigital;
import br.tec.dev2b.app.certificado.repository.CertificadoDigitalRepository;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.infra.minio.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificadoDigitalService {

    private final CertificadoDigitalRepository certificadoRepository;
    private final EmpresaRepository empresaRepository;
    private final MinioService minioService;

    private static final String BUCKET_CERTIFICADOS = "certificados-digitais";

    @Transactional
    public CertificadoDigitalDto upload(UUID empresaId, MultipartFile file, String senha) {
        log.info("Iniciando upload de certificado para empresa: {}", empresaId);

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + empresaId));

        // Validar arquivo
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        String nomeOriginal = file.getOriginalFilename();
        if (nomeOriginal == null || (!nomeOriginal.endsWith(".p12") && !nomeOriginal.endsWith(".pfx"))) {
            throw new IllegalArgumentException("Formato inválido. Use .p12 ou .pfx");
        }

        // Validar tamanho (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Arquivo muito grande. Máximo 2MB");
        }

        try {
            // Desativar certificado anterior se existir
            Optional<CertificadoDigital> certAnterior = certificadoRepository.findByEmpresaIdAndAtivoTrue(empresaId);
            certAnterior.ifPresent(cert -> {
                cert.setAtivo(false);
                certificadoRepository.save(cert);
                log.info("Certificado anterior desativado: {}", cert.getId());
            });

            // Gerar nome único para o arquivo
            String nomeArquivo = UUID.randomUUID() + "_" + nomeOriginal;

            // Upload no MinIO
            String objectName = minioService.upload(BUCKET_CERTIFICADOS, nomeArquivo, file);
            String urlMinio = minioService.getPublicUrl(BUCKET_CERTIFICADOS, objectName);
            log.info("Certificado enviado ao MinIO: {}", urlMinio);

            // Hash da senha (SHA-256)
            String senhaHash = hashSenha(senha);

            // Salvar no banco
            CertificadoDigital certificado = CertificadoDigital.builder()
                    .empresa(empresa)
                    .nomeArquivo(nomeOriginal)
                    .urlMinio(urlMinio)
                    .senhaHash(senhaHash)
                    .tamanhoBytes(file.getSize())
                    .ativo(true)
                    .build();

            CertificadoDigital saved = certificadoRepository.save(certificado);
            log.info("Certificado salvo no banco: {}", saved.getId());

            return CertificadoDigitalDto.from(saved);

        } catch (Exception e) {
            log.error("Erro ao fazer upload do certificado", e);
            throw new RuntimeException("Erro ao fazer upload do certificado: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<CertificadoDigitalDto> buscarAtivoPorEmpresa(UUID empresaId) {
        return certificadoRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .map(CertificadoDigitalDto::from);
    }

    @Transactional
    public void deletar(UUID id) {
        CertificadoDigital certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Certificado não encontrado: " + id));

        certificado.setAtivo(false);
        certificadoRepository.save(certificado);
        log.info("Certificado desativado: {}", id);
    }

    public boolean validarSenha(UUID certificadoId, String senha) {
        CertificadoDigital certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new IllegalArgumentException("Certificado não encontrado"));

        return hashSenha(senha).equals(certificado.getSenhaHash());
    }

    private String hashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
}
