package com.ufcg.psoft.project.service.ranking;

import com.ufcg.psoft.project.dto.ranking.RankingResponseDTO;

import java.util.List;

public interface RankingService {

    List<RankingResponseDTO> gerarRankingDoGrupo(Long grupoId);

    List<RankingResponseDTO> gerarRankingGlobal();

}