package com.ufcg.psoft.project.dto.partida;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.partida.status.TipoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartidaResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("timeMandante")
    private String timeMandante;

    @JsonProperty("timeVisitante")
    private String timeVisitante;

    @JsonProperty("golsMandante")
    private Integer golsMandante;

    @JsonProperty("golsVisitante")
    private Integer golsVisitante;

    @JsonProperty("dataHora")
    private LocalDateTime dataHora;

    @JsonProperty("campeonatoNome")
    private String campeonatoNome;

    @JsonProperty("status")
    private TipoStatus status;

    private java.math.BigDecimal oddCasa;
    private java.math.BigDecimal oddEmpate;
    private java.math.BigDecimal oddFora;

    public static PartidaResponseDTO de(com.ufcg.psoft.project.model.partida.Partida p) {
        return new PartidaResponseDTO(p.getId(), p.getTimeMandante(), p.getTimeVisitante(), p.getGolsMandante(),
                p.getGolsVisitante(), p.getDataHora(), p.getCampeonato().getNome(), p.getStatus().getTipoStatus(),
                p.odd(com.ufcg.psoft.project.model.aposta.Palpite1X2.CASA),
                p.odd(com.ufcg.psoft.project.model.aposta.Palpite1X2.EMPATE),
                p.odd(com.ufcg.psoft.project.model.aposta.Palpite1X2.FORA));
    }
}
