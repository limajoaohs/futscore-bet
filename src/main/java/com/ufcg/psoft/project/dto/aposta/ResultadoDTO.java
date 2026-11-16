package com.ufcg.psoft.project.dto.aposta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResultadoDTO(@NotNull @Min(0) Integer golsMandante, @NotNull @Min(0) Integer golsVisitante) {}
