package com.ufcg.psoft.project.dto.palpite;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalpiteResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("golsMandante")
    private Integer golsMandante;

    @JsonProperty("golsVisitante")
    private Integer golsVisitante;

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("usuarioUsername")
    private String usuarioUsername;

    @JsonProperty("partidaId")
    private Long partidaId;

    @JsonProperty("partidaTimeMandante")
    private String partidaTimeMandante;

    @JsonProperty("partidaTimeVisitante")
    private String partidaTimeVisitante;

    @JsonProperty("grupoId")
    private Long grupoId;

    @JsonProperty("grupoNome")
    private String grupoNome;
}
