package com.ufcg.psoft.project.dto.convite;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConviteRequestDTO {

    @NotNull(message = "GrupoId obrigatorio")
    private Long grupoId;

    @NotNull(message = "OrganizadorId obrigatorio")
    private Long organizadorId;

    @NotNull(message = "ConvidadoId obrigatorio")
    private Long convidadoId;
}
