package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.RegraPontuacaoRequestDTO;
import com.ufcg.psoft.project.dto.RegraPontuacaoResponseDTO;
import com.ufcg.psoft.project.model.RegraPontuacao;
import com.ufcg.psoft.project.service.regrapontuacao.RegraPontuacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grupos")
public class RegraPontuacaoController {

    @Autowired
    private RegraPontuacaoService regraPontuacaoService;

    @PutMapping("/{grupoId}/regras-pontuacao")
    public ResponseEntity<RegraPontuacaoResponseDTO> configurarRegras(
            @PathVariable Long grupoId,
            @RequestParam Long usuarioId,
            @Valid @RequestBody RegraPontuacaoRequestDTO requestDTO) {
        RegraPontuacao regra = regraPontuacaoService.configurarRegraPontuacao(grupoId, usuarioId, requestDTO);
        return ResponseEntity.ok(new RegraPontuacaoResponseDTO(regra));
    }
}
