package com.ufcg.psoft.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "regra_pontuacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegraPontuacao {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regra_pontuacao_seq")
    @SequenceGenerator(name = "regra_pontuacao_seq", allocationSize = 50)
    private Long id;

    @JsonProperty("pontosAcertoVencedor")
    @Column(nullable = false)
    private Integer pontosAcertoVencedor;

    @JsonProperty("pontosAcertoEmpate")
    @Column(nullable = false)
    private Integer pontosAcertoEmpate;

    @JsonProperty("pontosPlacarExato")
    @Column(nullable = false)
    private Integer pontosPlacarExato;

    @JsonProperty("pontosBonusRodada")
    @Column(nullable = false)
    private Integer pontosBonusRodada;

    @JsonProperty("pontosBonusMataMata")
    @Column(nullable = false)
    private Integer pontosBonusMataMata;
}
