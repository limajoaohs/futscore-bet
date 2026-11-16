package com.ufcg.psoft.project.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampeonatoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("urlApi")
    private String urlApi;

    @JsonProperty("apiId")
    private Long apiId;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("ano")
    private String ano;

    @JsonProperty("ativo")
    private boolean ativo;
}
