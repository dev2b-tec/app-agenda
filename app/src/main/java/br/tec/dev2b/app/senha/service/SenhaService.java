package br.tec.dev2b.app.senha.service;

import br.tec.dev2b.app.senha.dto.AtualizarSenhaDto;
import br.tec.dev2b.app.senha.dto.CriarSenhaDto;
import br.tec.dev2b.app.senha.dto.SenhaDto;
import br.tec.dev2b.app.senha.model.Senha;
import br.tec.dev2b.app.senha.repository.SenhaRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SenhaService {

    private final SenhaRepository senhaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public SenhaDto criar(CriarSenhaDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + dto.getUsuarioId()));

        Senha senha = Senha.builder()
                .tipo(dto.getTipo())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .usuario(usuario)
                .build();

        return SenhaDto.from(senhaRepository.save(senha));
    }

    @Transactional(readOnly = true)
    public List<SenhaDto> listarPorUsuario(UUID usuarioId) {
        return senhaRepository.findByUsuarioId(usuarioId).stream()
                .map(SenhaDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SenhaDto> listarPorUsuarioETipo(UUID usuarioId, String tipo) {
        return senhaRepository.findByUsuarioIdAndTipo(usuarioId, tipo).stream()
                .map(SenhaDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SenhaDto buscarPorId(UUID id) {
        return senhaRepository.findById(id)
                .map(SenhaDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Senha não encontrada: " + id));
    }

    @Transactional
    public SenhaDto atualizar(UUID id, AtualizarSenhaDto dto) {
        Senha senha = senhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Senha não encontrada: " + id));

        if (dto.getTipo() != null) senha.setTipo(dto.getTipo());
        if (dto.getDescricao() != null) senha.setDescricao(dto.getDescricao());
        if (dto.getValor() != null) senha.setValor(dto.getValor());

        return SenhaDto.from(senhaRepository.save(senha));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!senhaRepository.existsById(id)) {
            throw new IllegalArgumentException("Senha não encontrada: " + id);
        }
        senhaRepository.deleteById(id);
    }
}
