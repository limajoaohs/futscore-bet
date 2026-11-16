package com.ufcg.psoft.project.service.aposta;

import com.ufcg.psoft.project.dto.aposta.ApostaRequestDTO;
import com.ufcg.psoft.project.dto.aposta.ApostaResponseDTO;
import com.ufcg.psoft.project.dto.aposta.ResultadoDTO;
import com.ufcg.psoft.project.dto.partida.PartidaResponseDTO;

import java.util.List;

public interface ApostaService {
    List<PartidaResponseDTO> listarPartidasAbertas();
    ApostaResponseDTO apostar(Long usuarioId, String codigoAcesso, ApostaRequestDTO dto);
    List<ApostaResponseDTO> listarApostas(Long usuarioId, String codigoAcesso);
    PartidaResponseDTO registrarResultado(Long partidaId, Long adminId, String codigoAcesso, ResultadoDTO dto);
    int liquidarApostas();
}
