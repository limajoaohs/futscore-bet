package com.ufcg.psoft.project.dto.grupo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.grupo.Privacidade;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @JsonProperty("descricao")
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @JsonProperty("privacidade")
    @NotNull(message = "A privacidade é obrigatória")
    private Privacidade privacidade;

    @JsonProperty("limiteParticipantes")
    @NotNull(message = "O limite de participantes é obrigatório")
    @Positive(message = "O limite deve ser maior que zero")
    private Integer limiteParticipantes;

    @NotNull(message = "O ID do campeonato é obrigatório")
    private Long campeonatoId;

    @NotNull(message = "O ID do criador é obrigatório")
    private Long criadorId;

    @JsonProperty("minutosParaAbertura")
    @Min(value = 0, message = "O tempo de abertura não pode ser negativo")
    private Integer minutosParaAbertura;

    @JsonProperty("minutosParaFechamento")
    @Min(value = 0, message = "O tempo de fechamento não pode ser negativo")
    private Integer minutosParaFechamento;
}