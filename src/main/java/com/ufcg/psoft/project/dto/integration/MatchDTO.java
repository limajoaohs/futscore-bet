package com.ufcg.psoft.project.dto.integration;

import com.ufcg.psoft.project.model.partida.status.TipoStatus;
import lombok.Data;

@Data
public class MatchDTO {
    private Long id;
    private String utcDate;
    private TipoStatus status;
    private TeamDTO homeTeam;
    private TeamDTO awayTeam;
    private ScoreDTO score;
}
