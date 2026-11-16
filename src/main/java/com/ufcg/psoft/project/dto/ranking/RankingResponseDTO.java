package com.ufcg.psoft.project.dto.ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseDTO {
    private Integer posicao;
    private String usuarioNome;
    private String usuarioUsername;
    private int pontuacaoTotal;
    private int quantidadePlacaresExatos;
    private int quantidadeAcertosVencedor;
    private int quantidadeAcertosEmpate;
}