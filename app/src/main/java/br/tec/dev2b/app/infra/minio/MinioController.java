package br.tec.dev2b.app.infra.minio;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class MinioController {

    private final MinioService minioService;

    @GetMapping("/fotos/**")
    public ResponseEntity<byte[]> getFoto(HttpServletRequest request) {
        String path = request.getRequestURI().substring("/api/v1/files/fotos/".length());
        try {
            byte[] arquivo = minioService.downloadArquivo(minioService.getBucketFotos(), path);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(determinarContentType(path));
            headers.setCacheControl("public, max-age=31536000");
            
            return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/documentos/**")
    public ResponseEntity<byte[]> getDocumento(HttpServletRequest request) {
        String path = request.getRequestURI().substring("/api/v1/files/documentos/".length());
        try {
            byte[] arquivo = minioService.downloadArquivo(minioService.getBucketDocumentos(), path);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(determinarContentType(path));
            headers.setCacheControl("public, max-age=31536000");
            
            return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private MediaType determinarContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
