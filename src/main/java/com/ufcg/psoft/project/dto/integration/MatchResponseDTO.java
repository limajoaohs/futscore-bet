package com.ufcg.psoft.project.dto.integration;

import lombok.Data;
import java.util.List;

@Data
public class MatchResponseDTO {
    private List<MatchDTO> matches;
}
