package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.convite.ConviteRequestDTO;
import com.ufcg.psoft.project.service.convite.ConviteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/convites",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ConviteController {

    @Autowired
    ConviteService conviteService;

    @PostMapping
    public ResponseEntity<?> enviarConvite(
            @RequestBody @Valid ConviteRequestDTO conviteRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(conviteService.enviarConvite(conviteRequestDTO));
    }

    @GetMapping("/pendentes/{usuarioId}")
    public ResponseEntity<?> listarPendentes(
            @PathVariable Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(conviteService.listarConvitesPendentes(usuarioId));
    }

    @PutMapping("/{conviteId}/responder")
    public ResponseEntity<?> responderConvite(
            @PathVariable Long conviteId,
            @RequestParam Long usuarioId,
            @RequestParam String codigoAcesso,
            @RequestParam String acao) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(conviteService.responderConvite(conviteId, usuarioId, codigoAcesso, acao));
    }
}
