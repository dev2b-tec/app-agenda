package br.tec.dev2b.app.infra.minio;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.public-url}")
    private String publicUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket.fotos}")
    private String bucketFotos;

    @Value("${minio.bucket.documentos}")
    private String bucketDocumentos;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * Client used exclusively for presigned URL generation.
     * Uses the public URL so the generated signature matches the host the browser accesses.
     * getPresignedObjectUrl() does NOT make network calls — safe even if publicUrl is external.
     */
    @Bean
    public MinioClient minioClientPublic() {
        String endpoint = (publicUrl != null && !publicUrl.isBlank()) ? publicUrl : url;
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
