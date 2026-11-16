package com.ufcg.psoft.project.dto.campeonato;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampeonatoRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome do campeonato e obrigatorio")
    private String nome;

    @JsonProperty("urlApi")
    @NotBlank(message = "URL da API e obrigatoria")
    private String urlApi;

    @JsonProperty("ano")
    @NotBlank(message = "Ano e obrigatorio")
    private String ano;

    @JsonProperty("comecaAutomatico")
    @Builder.Default
    private boolean ativo = true;
}