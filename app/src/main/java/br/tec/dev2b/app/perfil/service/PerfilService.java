package br.tec.dev2b.app.perfil.service;

import br.tec.dev2b.app.perfil.dto.PerfilDto;
import br.tec.dev2b.app.perfil.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public List<PerfilDto> listar() {
        return perfilRepository.findAll().stream().map(PerfilDto::from).toList();
    }

    public PerfilDto buscarPorId(Long id) {
        return perfilRepository.findById(id)
                .map(PerfilDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado: " + id));
    }
}
