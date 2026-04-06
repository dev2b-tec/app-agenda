package br.tec.dev2b.app.siteconfig.model;

import br.tec.dev2b.app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "site_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "link_slug")
    private String linkSlug;

    @Column(name = "cor_principal")
    private String corPrincipal;

    @Column(nullable = false)
    private String disponibilidade = "ativo";

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "perfil_url")
    private String perfilUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "sobre_mim", columnDefinition = "TEXT")
    private String sobreMim;

    @Column(columnDefinition = "TEXT")
    private String servicos;

    @Column
    private String slogan;

    @Column(name = "titulo_pagina")
    private String tituloPagina;

    @Column
    private String whatsapp;

    @Column(name = "email_contato")
    private String emailContato;

    @Column
    private String telefone;

    @Column(name = "website_link")
    private String websiteLink;

    @Column
    private String instagram;

    @Column
    private String facebook;

    @Column
    private String linkedin;

    @Column
    private String youtube;

    @OneToMany(mappedBy = "siteConfig", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BlogPost> posts = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}
