package br.tec.dev2b.app.menuativo.repository;

import br.tec.dev2b.app.menuativo.model.MenuAtivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuAtivoRepository extends JpaRepository<MenuAtivo, Long> {

    Optional<MenuAtivo> findByChave(String chave);
}
