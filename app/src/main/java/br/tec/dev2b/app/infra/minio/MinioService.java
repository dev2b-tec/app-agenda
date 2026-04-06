package br.tec.dev2b.app.infra.minio;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String upload(String bucket, String objectName, MultipartFile file) {
        try {
            garantirBucket(bucket);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload para MinIO: " + e.getMessage(), e);
        }
    }

    public String gerarUrlTemporaria(String bucket, String objectName, int expiracaoMinutos) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectName)
                    .expiry(expiracaoMinutos, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
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
        return gerarUrlTemporaria(bucket, objectName, 60 * 24 * 7);
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
