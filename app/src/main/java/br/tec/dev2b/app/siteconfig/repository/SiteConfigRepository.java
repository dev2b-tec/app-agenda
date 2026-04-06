package br.tec.dev2b.app.siteconfig.repository;

import br.tec.dev2b.app.siteconfig.model.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SiteConfigRepository extends JpaRepository<SiteConfig, UUID> {
    Optional<SiteConfig> findByUsuarioId(UUID usuarioId);
}
