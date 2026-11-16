package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.campeonato.CampeonatoRequestDTO;
import com.ufcg.psoft.project.service.campeonato.CampeonatoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/campeonatos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class CampeonatoController {

    @Autowired
    CampeonatoService campeonatoService;

    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarCampeonatos(
            @RequestParam Long adminId,
            @RequestParam String codigoAcesso) {
        campeonatoService.sincronizarCampeonatos(adminId, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Sincronizacao realizada com sucesso!");
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCampeonato(
            @RequestBody @Valid CampeonatoRequestDTO campeonatoRequestDTO,
            @RequestParam Long adminId,
            @RequestParam String codigoAcesso) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(campeonatoService.cadastrarCampeonato(campeonatoRequestDTO, adminId, codigoAcesso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam String codigoAcesso) {
        campeonatoService.removerCampeonato(id, adminId, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(
        @PathVariable Long id,
        @RequestParam Long adminId,
        @RequestParam String codigoAcesso) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(campeonatoService.alterarStatus(id, true, adminId, codigoAcesso));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(
        @PathVariable Long id,
        @RequestParam Long adminId,
        @RequestParam String codigoAcesso) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(campeonatoService.alterarStatus(id, false, adminId, codigoAcesso));
    }
}