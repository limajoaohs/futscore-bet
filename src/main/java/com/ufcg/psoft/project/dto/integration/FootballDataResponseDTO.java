package com.ufcg.psoft.project.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class FootballDataResponseDTO {

    @JsonProperty("competitions")
    private List<CompetitionDTO> competitions;
}
