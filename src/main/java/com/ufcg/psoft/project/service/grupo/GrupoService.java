package com.ufcg.psoft.project.service.grupo;

import com.ufcg.psoft.project.dto.grupo.GrupoRequestDTO;
import com.ufcg.psoft.project.dto.grupo.GrupoResponseDTO;
import com.ufcg.psoft.project.dto.grupo.CriteriosDesempateDTO;

import java.util.List;

public interface GrupoService {

    GrupoResponseDTO criarGrupo(GrupoRequestDTO grupoRequestDTO);

    GrupoResponseDTO findById(Long Id);

    List<GrupoResponseDTO> findByNome(String nome);

    List<GrupoResponseDTO> findAll();

    GrupoResponseDTO adicionarParticipanteAGrupoPublico(Long grupoId, Long usuarioId);

    GrupoResponseDTO configurarCriteriosDesempate(Long grupoId, Long usuarioId, CriteriosDesempateDTO dto);
}
