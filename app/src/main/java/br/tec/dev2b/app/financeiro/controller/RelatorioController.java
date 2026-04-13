package br.tec.dev2b.app.financeiro.controller;

import br.tec.dev2b.app.evolucao.service.RelatorioEvolucoesService;
import br.tec.dev2b.app.financeiro.dto.MovimentoFinanceiroDto;
import br.tec.dev2b.app.financeiro.service.RelatorioContasPagarService;
import br.tec.dev2b.app.financeiro.service.RelatorioContasReceberService;
import br.tec.dev2b.app.financeiro.service.RelatorioMensalidadesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioContasPagarService pagarService;
    private final RelatorioContasReceberService receberService;
    private final RelatorioMensalidadesService mensalidadesService;
    private final RelatorioEvolucoesService evolucoesService;

    @GetMapping("/contas-a-pagar/pdf")
    public ResponseEntity<byte[]> pdf(@RequestParam UUID empresaId) throws IOException {
        List<MovimentoFinanceiroDto> dados = pagarService.listar(empresaId);
        byte[] bytes = pagarService.gerarPdf(dados);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contas_a_pagar.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping("/contas-a-pagar/excel")
    public ResponseEntity<byte[]> excel(@RequestParam UUID empresaId) throws IOException {
        List<MovimentoFinanceiroDto> dados = pagarService.listar(empresaId);
        byte[] bytes = pagarService.gerarExcel(dados);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contas_a_pagar.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping("/contas-a-pagar/csv")
    public ResponseEntity<byte[]> csv(@RequestParam UUID empresaId) throws IOException {
        List<MovimentoFinanceiroDto> dados = pagarService.listar(empresaId);
        byte[] bytes = pagarService.gerarCsv(dados).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contas_a_pagar.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping(value = "/contas-a-pagar/imprimir", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> imprimir(@RequestParam UUID empresaId) {
        List<MovimentoFinanceiroDto> dados = pagarService.listar(empresaId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(pagarService.gerarHtml(dados));
    }


    @GetMapping("/contas-a-receber/pdf")
    public ResponseEntity<byte[]> pdfReceber(@RequestParam UUID empresaId) throws IOException {
        byte[] bytes = receberService.gerarPdf(empresaId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contas_a_receber.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping("/contas-a-receber/excel")
    public ResponseEntity<byte[]> excelReceber(@RequestParam UUID empresaId) throws IOException {
        byte[] bytes = receberService.gerarExcel(empresaId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contas_a_receber.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping("/contas-a-receber/csv")
    public ResponseEntity<byte[]> csvReceber(@RequestParam UUID empresaId) throws IOException {
        byte[] bytes = receberService.gerarCsv(empresaId).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contas_a_receber.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping(value = "/contas-a-receber/imprimir", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> imprimirReceber(@RequestParam UUID empresaId) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(receberService.gerarHtml(empresaId));
    }


    @GetMapping("/mensalidades/pdf")
    public ResponseEntity<byte[]> pdfMensalidades(@RequestParam UUID empresaId) throws IOException {
        List<MovimentoFinanceiroDto> dados = mensalidadesService.listar(empresaId);
        byte[] bytes = mensalidadesService.gerarPdf(dados);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mensalidades.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping("/mensalidades/excel")
    public ResponseEntity<byte[]> excelMensalidades(@RequestParam UUID empresaId) throws IOException {
        List<MovimentoFinanceiroDto> dados = mensalidadesService.listar(empresaId);
        byte[] bytes = mensalidadesService.gerarExcel(dados);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mensalidades.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping("/mensalidades/csv")
    public ResponseEntity<byte[]> csvMensalidades(@RequestParam UUID empresaId) throws IOException {
        List<MovimentoFinanceiroDto> dados = mensalidadesService.listar(empresaId);
        byte[] bytes = mensalidadesService.gerarCsv(dados).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mensalidades.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping(value = "/mensalidades/imprimir", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> imprimirMensalidades(@RequestParam UUID empresaId) {
        List<MovimentoFinanceiroDto> dados = mensalidadesService.listar(empresaId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(mensalidadesService.gerarHtml(dados));
    }

    // ─── Evoluções ────────────────────────────────────────────────────────────

    @GetMapping("/evolucoes/pdf")
    public ResponseEntity<byte[]> pdfEvolucoes(@RequestParam UUID pacienteId) throws IOException {
        byte[] bytes = evolucoesService.gerarPdf(pacienteId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"evolucoes.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(bytes);
    }

    @GetMapping(value = "/evolucoes/imprimir", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> imprimirEvolucoes(@RequestParam UUID pacienteId) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(evolucoesService.gerarHtml(pacienteId));
    }
}

