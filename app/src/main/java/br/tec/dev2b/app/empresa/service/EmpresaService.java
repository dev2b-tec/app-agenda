package br.tec.dev2b.app.empresa.service;

import br.tec.dev2b.app.agenda.model.Agenda;
import br.tec.dev2b.app.agenda.repository.AgendaRepository;
import br.tec.dev2b.app.empresa.dto.AtualizarEmpresaDto;
import br.tec.dev2b.app.empresa.dto.CriarEmpresaDto;
import br.tec.dev2b.app.empresa.dto.EmpresaDto;
import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.empresa.repository.EmpresaRepository;
import br.tec.dev2b.app.infra.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final AgendaRepository agendaRepository;
    private final MinioService minioService;

    @Transactional
    public EmpresaDto criar(CriarEmpresaDto dto) {
        Empresa empresa = Empresa.builder()
                .nomeComercial(dto.getNomeComercial())
                .telefoneComercial(dto.getTelefoneComercial())
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .duracaoSessaoMinutos(dto.getDuracaoSessaoMinutos())
                .build();

        return EmpresaDto.from(empresaRepository.save(empresa));
    }

    @Transactional(readOnly = true)
    public List<EmpresaDto> listar() {
        return empresaRepository.findAll().stream().map(EmpresaDto::from).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaDto buscarPorId(UUID id) {
        return empresaRepository.findById(id)
                .map(EmpresaDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + id));
    }

    @Transactional
    public EmpresaDto atualizar(UUID id, AtualizarEmpresaDto dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + id));

        if (dto.getNomeComercial() != null) empresa.setNomeComercial(dto.getNomeComercial());
        if (dto.getTelefoneComercial() != null) empresa.setTelefoneComercial(dto.getTelefoneComercial());
        if (dto.getCep() != null) empresa.setCep(dto.getCep());
        if (dto.getLogradouro() != null) empresa.setLogradouro(dto.getLogradouro());
        if (dto.getNumero() != null) empresa.setNumero(dto.getNumero());
        if (dto.getComplemento() != null) empresa.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null) empresa.setBairro(dto.getBairro());
        if (dto.getCidade() != null) empresa.setCidade(dto.getCidade());
        if (dto.getDuracaoSessaoMinutos() != null) empresa.setDuracaoSessaoMinutos(dto.getDuracaoSessaoMinutos());
        if (dto.getAgendaId() != null) {
            Agenda agenda = agendaRepository.findById(dto.getAgendaId())
                    .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada: " + dto.getAgendaId()));
            empresa.setAgenda(agenda);
        }

        return EmpresaDto.from(empresaRepository.save(empresa));
    }

    @Transactional
    public String uploadLogo(UUID id, MultipartFile file) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada: " + id));

        String ext = obterExtensao(file.getOriginalFilename());
        String objectName = "empresas/" + id + "/logo" + ext;
        String bucket = minioService.getBucketFotos();

        minioService.upload(bucket, objectName, file);
        String url = minioService.getPublicUrl(bucket, objectName);

        empresa.setLogoUrl(url);
        empresaRepository.save(empresa);
        return url;
    }

    private String obterExtensao(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!empresaRepository.existsById(id)) {
            throw new IllegalArgumentException("Empresa não encontrada: " + id);
        }
        empresaRepository.deleteById(id);
    }
}
