package com.ufcg.psoft.project.service.palpite;

import com.ufcg.psoft.project.dto.palpite.PalpiteRequestDTO;
import com.ufcg.psoft.project.dto.palpite.PalpiteResponseDTO;

public interface PalpiteService {
    PalpiteResponseDTO registrarPalpite(PalpiteRequestDTO palpiteRequestDTO);

    PalpiteResponseDTO editarPalpite(Long id, PalpiteRequestDTO palpiteRequestDTO);
}
