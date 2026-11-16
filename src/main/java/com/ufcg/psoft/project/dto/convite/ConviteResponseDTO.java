package com.ufcg.psoft.project.dto.convite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.convite.StatusConvite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConviteResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("grupoId")
    private Long grupoId;

    @JsonProperty("organizadorId")
    private Long organizadorId;

    @JsonProperty("convidadoId")
    private Long convidadoId;

    @JsonProperty("status")
    private StatusConvite status;
}
