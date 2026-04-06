package br.tec.dev2b.app.siteconfig.repository;

import br.tec.dev2b.app.siteconfig.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    List<BlogPost> findBySiteConfigIdOrderByCreatedAtDesc(UUID siteConfigId);
}
