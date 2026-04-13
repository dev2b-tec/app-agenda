package br.tec.dev2b.app.evolucao.service;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.evolucao.model.Evolucao;
import br.tec.dev2b.app.evolucao.repository.EvolucaoRepository;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.paciente.repository.PacienteRepository;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioEvolucoesService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EvolucaoRepository evolucaoRepository;
    private final PacienteRepository pacienteRepository;
    private final TemplateEngine templateEngine;

    public record EvolucaoVm(
            int ordem,
            String data,
            String profissional,
            String titulo,
            String resumoAi,
            String comentariosGerais,
            String examesRealizados,
            String prescricao,
            String conduta
    ) {}

    @Transactional(readOnly = true)
    public byte[] gerarPdf(UUID pacienteId) throws IOException {
        String html = renderHtml(pacienteId);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(toXhtml(html));
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Erro ao gerar PDF de evoluções", e);
        }
    }

    @Transactional(readOnly = true)
    public String gerarHtml(UUID pacienteId) {
        return renderHtml(pacienteId);
    }

    // -------------------------------------------------------------------------

    private String renderHtml(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + pacienteId));

        Empresa empresa = paciente.getEmpresa();

        // Sort ascending (oldest → newest) so numbering matches chronological order
        List<Evolucao> evolucoes = new ArrayList<>(
                evolucaoRepository.findByPacienteIdOrderByDataDesc(pacienteId)
        );
        java.util.Collections.reverse(evolucoes);

        List<EvolucaoVm> items = new ArrayList<>();
        for (int i = 0; i < evolucoes.size(); i++) {
            Evolucao ev = evolucoes.get(i);
            items.add(new EvolucaoVm(
                    i + 1,
                    ev.getData() != null ? ev.getData().format(DATE_FMT) : "",
                    ev.getProfissional() != null ? ev.getProfissional() : "",
                    ev.getTitulo(),
                    ev.getResumoAi(),
                    ev.getComentariosGerais(),
                    ev.getExamesRealizados(),
                    ev.getPrescricao(),
                    ev.getConduta()
            ));
        }

        Context ctx = new Context(Locale.forLanguageTag("pt-BR"));
        ctx.setVariable("pacienteNome", paciente.getNome());
        ctx.setVariable("empresaNome", empresa != null ? empresa.getNomeComercial() : "");
        ctx.setVariable("logoUrl", empresa != null ? empresa.getLogoUrl() : null);
        ctx.setVariable("evolucoes", items);

        return templateEngine.process("relatorio-evolucoes", ctx);
    }

    /** Converts Thymeleaf HTML5 output to valid XHTML required by Flying Saucer. */
    private String toXhtml(String html) {
        Document doc = Jsoup.parse(html);
        doc.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .charset(java.nio.charset.StandardCharsets.UTF_8);
        return doc.html();
    }
}
