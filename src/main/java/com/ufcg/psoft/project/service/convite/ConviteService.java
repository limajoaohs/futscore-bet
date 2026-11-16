package com.ufcg.psoft.project.service.convite;

import com.ufcg.psoft.project.dto.convite.ConviteRequestDTO;
import com.ufcg.psoft.project.dto.convite.ConviteResponseDTO;

import java.util.List;

public interface ConviteService {

    ConviteResponseDTO enviarConvite(ConviteRequestDTO conviteDTO);

    List<ConviteResponseDTO> listarConvitesPendentes(Long usuarioId);

    ConviteResponseDTO responderConvite(Long conviteId, Long usuarioId, String codigoAcesso, String acao);
}
