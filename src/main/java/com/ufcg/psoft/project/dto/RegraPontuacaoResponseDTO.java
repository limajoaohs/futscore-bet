package com.ufcg.psoft.project.dto;

import com.ufcg.psoft.project.model.RegraPontuacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegraPontuacaoResponseDTO {

    private Long id;
    private Integer pontosAcertoVencedor;
    private Integer pontosAcertoEmpate;
    private Integer pontosPlacarExato;
    private Integer pontosBonusRodada;
    private Integer pontosBonusMataMata;

    public RegraPontuacaoResponseDTO(RegraPontuacao regra) {
        if (regra != null) {
            this.id = regra.getId();
            this.pontosAcertoVencedor = regra.getPontosAcertoVencedor();
            this.pontosAcertoEmpate = regra.getPontosAcertoEmpate();
            this.pontosPlacarExato = regra.getPontosPlacarExato();
            this.pontosBonusRodada = regra.getPontosBonusRodada();
            this.pontosBonusMataMata = regra.getPontosBonusMataMata();
        }
    }
}
