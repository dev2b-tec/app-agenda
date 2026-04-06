package br.tec.dev2b.app.certificado.repository;

import br.tec.dev2b.app.certificado.model.CertificadoDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificadoDigitalRepository extends JpaRepository<CertificadoDigital, UUID> {
    Optional<CertificadoDigital> findByEmpresaIdAndAtivoTrue(UUID empresaId);
}
