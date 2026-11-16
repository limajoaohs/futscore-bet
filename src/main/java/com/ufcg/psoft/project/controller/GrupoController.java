package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.grupo.CriteriosDesempateDTO;
import com.ufcg.psoft.project.dto.grupo.GrupoRequestDTO;
import com.ufcg.psoft.project.service.grupo.GrupoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/grupos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GrupoController {

    @Autowired
    GrupoService grupoService;

    @PostMapping
    public ResponseEntity<?> criarGrupo(
            @RequestBody @Valid GrupoRequestDTO grupoRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(grupoService.criarGrupo(grupoRequestDTO));
    }

    @GetMapping
    public ResponseEntity<?> listarGruposPublicos(
            @RequestParam(required = false, defaultValue = "") String nome) {
        if (nome != null && !nome.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(grupoService.findByNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(grupoService.findAll());
    }

    @PutMapping("/{grupoId}/entrar")
    public ResponseEntity<?> entrarEmGrupoPublico(
            @PathVariable Long grupoId,
            @RequestParam Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(grupoService.adicionarParticipanteAGrupoPublico(grupoId, usuarioId));
    }

    @PutMapping("/{grupoId}/criterios-desempate")
    public ResponseEntity<?> configurarCriteriosDesempate(
            @PathVariable Long grupoId,
            @RequestParam Long usuarioId,
            @RequestBody @Valid CriteriosDesempateDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(grupoService.configurarCriteriosDesempate(grupoId, usuarioId, requestDTO));
    }
}
