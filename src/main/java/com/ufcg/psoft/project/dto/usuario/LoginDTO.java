package com.ufcg.psoft.project.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(@NotBlank String email, @NotBlank String codigo) {}
