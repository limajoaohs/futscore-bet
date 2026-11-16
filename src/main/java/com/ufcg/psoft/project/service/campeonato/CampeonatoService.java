package com.ufcg.psoft.project.service.campeonato;

import com.ufcg.psoft.project.dto.campeonato.CampeonatoRequestDTO;
import com.ufcg.psoft.project.dto.campeonato.CampeonatoResponseDTO;

public interface CampeonatoService {

    void sincronizarCampeonatos(Long adminId, String codigoAcesso);

    CampeonatoResponseDTO cadastrarCampeonato(CampeonatoRequestDTO dto, Long adminId, String codigoAcesso);

    void removerCampeonato(Long id,Long adminId, String codigoAcesso);

    CampeonatoResponseDTO alterarStatus(Long id, boolean ativo,Long adminId, String codigoAcesso);
}
