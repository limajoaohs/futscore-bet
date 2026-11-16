package com.ufcg.psoft.project.model.campeonato;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campeonato")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campeonato {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campeonato_seq")
    @SequenceGenerator(name = "campeonato_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String urlApi;

    @JsonProperty("apiId")
    @Column(unique = true)
    private Long apiId;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("ano")
    @Column(nullable = false)
    private String ano;

    @JsonProperty("comecaAutomatico")
    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;
}
