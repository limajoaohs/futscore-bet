package com.ufcg.psoft.project.dto.palpite;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PalpiteRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "O ID da partida é obrigatório")
    private Long partidaId;

    @NotNull(message = "O ID do grupo é obrigatório")
    private Long grupoId;

    @NotNull(message = "A quantidade de gols do mandante é obrigatória")
    @Min(value = 0, message = "A quantidade de gols do mandante não pode ser negativa")
    private Integer golsMandante;

    @NotNull(message = "A quantidade de gols do visitante é obrigatória")
    @Min(value = 0, message = "A quantidade de gols do visitante não pode ser negativa")
    private Integer golsVisitante;
}
