package br.tec.dev2b.app.menuativo.service;

import br.tec.dev2b.app.menuativo.dto.MenuAtivoDto;
import br.tec.dev2b.app.menuativo.repository.MenuAtivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuAtivoService {

    private final MenuAtivoRepository menuAtivoRepository;

    /** Retorna todos os menus com seu status ativo/inativo. */
    public List<MenuAtivoDto> listar() {
        return menuAtivoRepository.findAll()
                .stream()
                .map(m -> new MenuAtivoDto(m.getChave(), m.getLabel(), m.getAtivo()))
                .toList();
    }

    /** Verifica se uma tela específica está habilitada. */
    public boolean isAtivo(String chave) {
        return menuAtivoRepository.findByChave(chave)
                .map(m -> Boolean.TRUE.equals(m.getAtivo()))
                .orElse(true);
    }
}
