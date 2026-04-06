package br.tec.dev2b.app.infra.minio;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String upload(String bucket, String objectName, MultipartFile file) {
        log.info("[MinioService.upload] bucket={} objectName={} size={} contentType={}",
                bucket, objectName, file.getSize(), file.getContentType());
        try {
            garantirBucket(bucket);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("[MinioService.upload] sucesso: bucket={} objectName={}", bucket, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("[MinioService.upload] erro: bucket={} objectName={} erro={}", bucket, objectName, e.getMessage(), e);
            throw new RuntimeException("Erro ao fazer upload para MinIO: " + e.getMessage(), e);
        }
    }

    public String gerarUrlTemporaria(String bucket, String objectName, int expiracaoMinutos) {
        log.info("[MinioService.gerarUrlTemporaria] bucket={} objectName={} expiracao={}min", bucket, objectName, expiracaoMinutos);
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectName)
                    .expiry(expiracaoMinutos, TimeUnit.MINUTES)
                    .build());
            log.info("[MinioService.gerarUrlTemporaria] URL gerada (raw): {}", url);
            return url;
        } catch (Exception e) {
            log.error("[MinioService.gerarUrlTemporaria] erro: bucket={} objectName={} erro={}", bucket, objectName, e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar URL temporária: " + e.getMessage(), e);
        }
    }

    public void deletar(String bucket, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar objeto do MinIO: " + e.getMessage(), e);
        }
    }

    private void garantirBucket(String bucket) {
        try {
            boolean existe = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!existe) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar/criar bucket: " + e.getMessage(), e);
        }
    }

    public String getBucketFotos() {
        return minioConfig.getBucketFotos();
    }

    public String getBucketDocumentos() {
        return minioConfig.getBucketDocumentos();
    }

    public String getPublicUrl(String bucket, String objectName) {
        String url = gerarUrlTemporaria(bucket, objectName, 60 * 24 * 7);
        // Replace internal MinIO host with the public URL so the browser can reach it
        if (!minioConfig.getUrl().equals(minioConfig.getPublicUrl())) {
            String before = url;
            url = url.replace(minioConfig.getUrl(), minioConfig.getPublicUrl());
            log.info("[MinioService.getPublicUrl] host reescrito: {} -> {}", minioConfig.getUrl(), minioConfig.getPublicUrl());
            log.debug("[MinioService.getPublicUrl] URL antes={} depois={}", before, url);
        } else {
            log.debug("[MinioService.getPublicUrl] minio.url == minio.public-url, nenhuma reescrita necessária");
        }
        return url;
    }

    public byte[] downloadArquivo(String bucket, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build()).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar arquivo do MinIO: " + e.getMessage(), e);
        }
    }
}
