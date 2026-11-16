package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.aposta.ApostaRequestDTO;
import com.ufcg.psoft.project.dto.aposta.ResultadoDTO;
import com.ufcg.psoft.project.service.aposta.ApostaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApostaController {

    private final ApostaService apostaService;

    @GetMapping("/partidas")
    public ResponseEntity<?> partidasAbertas() {
        return ResponseEntity.ok(apostaService.listarPartidasAbertas());
    }

    @PostMapping("/partidas/{id}/resultado")
    public ResponseEntity<?> registrarResultado(@PathVariable Long id,
                                                @RequestParam Long adminId,
                                                @RequestParam String codigoAcesso,
                                                @RequestBody @Valid ResultadoDTO dto) {
        return ResponseEntity.ok(apostaService.registrarResultado(id, adminId, codigoAcesso, dto));
    }

    @PostMapping("/apostas")
    public ResponseEntity<?> apostar(@RequestParam Long usuarioId,
                                     @RequestParam String codigoAcesso,
                                     @RequestBody @Valid ApostaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apostaService.apostar(usuarioId, codigoAcesso, dto));
    }

    @GetMapping("/apostas")
    public ResponseEntity<?> minhasApostas(@RequestParam Long usuarioId, @RequestParam String codigoAcesso) {
        return ResponseEntity.ok(apostaService.listarApostas(usuarioId, codigoAcesso));
    }
}
