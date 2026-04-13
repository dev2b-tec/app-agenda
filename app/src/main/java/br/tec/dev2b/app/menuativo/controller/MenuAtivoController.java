package br.tec.dev2b.app.menuativo.controller;

import br.tec.dev2b.app.menuativo.dto.MenuAtivoDto;
import br.tec.dev2b.app.menuativo.service.MenuAtivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-ativo")
@RequiredArgsConstructor
public class MenuAtivoController {

    private final MenuAtivoService menuAtivoService;

    /**
     * Retorna a lista de menus com o status ativo/inativo.
     * O frontend usa este endpoint para filtrar os itens do menu lateral.
     *
     * GET /api/v1/menu-ativo
     */
    @GetMapping
    public ResponseEntity<List<MenuAtivoDto>> listar() {
        return ResponseEntity.ok(menuAtivoService.listar());
    }
}
