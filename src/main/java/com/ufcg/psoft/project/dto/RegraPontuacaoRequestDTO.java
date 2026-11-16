package com.ufcg.psoft.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegraPontuacaoRequestDTO {

    @NotNull(message = "Pontos por acerto do vencedor é obrigatório")
    @PositiveOrZero(message = "Pontos por acerto do vencedor deve ser maior ou igual a zero")
    private Integer pontosAcertoVencedor;

    @NotNull(message = "Pontos por acerto de empate é obrigatório")
    @PositiveOrZero(message = "Pontos por acerto de empate deve ser maior ou igual a zero")
    private Integer pontosAcertoEmpate;

    @NotNull(message = "Pontos por placar exato é obrigatório")
    @PositiveOrZero(message = "Pontos por placar exato deve ser maior ou igual a zero")
    private Integer pontosPlacarExato;

    @NotNull(message = "Pontos por bônus de rodada é obrigatório")
    @PositiveOrZero(message = "Pontos por bônus de rodada deve ser maior ou igual a zero")
    private Integer pontosBonusRodada;

    @NotNull(message = "Pontos por bônus mata-mata é obrigatório")
    @PositiveOrZero(message = "Pontos por bônus mata-mata deve ser maior ou igual a zero")
    private Integer pontosBonusMataMata;
}
