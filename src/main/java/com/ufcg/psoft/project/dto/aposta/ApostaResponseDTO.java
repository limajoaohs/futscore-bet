package com.ufcg.psoft.project.dto.aposta;

import com.ufcg.psoft.project.model.aposta.Aposta;
import com.ufcg.psoft.project.model.aposta.Palpite1X2;
import com.ufcg.psoft.project.model.aposta.StatusAposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApostaResponseDTO(Long id, Long partidaId, String partida, Palpite1X2 palpite,
                                BigDecimal valor, BigDecimal odd, BigDecimal retornoPotencial,
                                StatusAposta status, LocalDateTime criadaEm, String placar) {

    public static ApostaResponseDTO de(Aposta a) {
        var p = a.getPartida();
        String placar = p.getGolsMandante() == null ? null : p.getGolsMandante() + " x " + p.getGolsVisitante();
        return new ApostaResponseDTO(a.getId(), p.getId(), p.getTimeMandante() + " x " + p.getTimeVisitante(),
                a.getPalpite(), a.getValor(), a.getOdd(), a.getRetornoPotencial(), a.getStatus(), a.getCriadaEm(), placar);
    }
}
