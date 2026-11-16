package com.ufcg.psoft.project.dto.aposta;

import com.ufcg.psoft.project.model.aposta.Palpite1X2;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApostaRequestDTO {
    @NotNull(message = "Partida obrigatoria")
    private Long partidaId;

    @NotNull(message = "Palpite obrigatorio")
    private Palpite1X2 palpite;

    @NotNull(message = "Valor obrigatorio")
    @DecimalMin(value = "1.00", message = "Aposta minima: 1.00")
    @DecimalMax(value = "10000.00", message = "Aposta maxima: 10000.00")
    private BigDecimal valor;
}
