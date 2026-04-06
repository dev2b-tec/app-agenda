package br.tec.dev2b.app.empresa.repository;

import br.tec.dev2b.app.empresa.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
}
