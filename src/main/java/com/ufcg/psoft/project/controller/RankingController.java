package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.ranking.RankingResponseDTO;
import com.ufcg.psoft.project.service.ranking.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rankings")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping("/global")
    public ResponseEntity<List<RankingResponseDTO>> obterRankingGlobal() {
        List<RankingResponseDTO> ranking = rankingService.gerarRankingGlobal();
        return ResponseEntity.status(HttpStatus.OK).body(ranking);
    }

    @GetMapping("/grupos/{grupoId}")
    public ResponseEntity<List<RankingResponseDTO>> obterRankingDoGrupo(@PathVariable Long grupoId) {
        List<RankingResponseDTO> ranking = rankingService.gerarRankingDoGrupo(grupoId);
        return ResponseEntity.status(HttpStatus.OK).body(ranking);
    }
}