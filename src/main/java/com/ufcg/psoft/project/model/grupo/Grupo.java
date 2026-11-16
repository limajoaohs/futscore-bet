package com.ufcg.psoft.project.model.grupo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.model.RegraPontuacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grupo {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("descricao")
    @Column(nullable = false)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "campeonato_id", nullable = false)
    @JsonProperty("campeonato")
    private Campeonato campeonato;

    @ManyToOne
    @JoinColumn(name = "organizador_id", nullable = false)
    @JsonProperty("organizador")
    private Usuario organizador;

    @JsonProperty("privacidade")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Privacidade privacidade;

    @JsonProperty("limiteParticipantes")
    @Column(nullable = false)
    private Integer limiteParticipantes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "regra_pontuacao_id", referencedColumnName = "id")
    @JsonProperty("regraPontuacao")
    private RegraPontuacao regraPontuacao;

    @JsonProperty("participantes")
    @ManyToMany
    @Builder.Default
    private List<Usuario> participantes = new ArrayList<>();

    @JsonProperty("criteriosDesempate")
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "grupo_criterios_desempate", joinColumns = @JoinColumn(name = "grupo_id"))
    @Column(name = "criterio")
    @OrderColumn(name = "prioridade")
    private List<String> criteriosDesempate = new ArrayList<>(List.of(
            "MAIOR_PLACAR_EXATO",
            "MENOR_ERRO",
            "MAIOR_ACERTO_VENCEDOR",
            "MAIOR_ACERTO_EMPATE"
    ));

    @JsonProperty("minutosParaAbertura")
    @Builder.Default
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int minutosParaAbertura = 0;

    @JsonProperty("tempoFechamento")
    @Builder.Default
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int minutosParaFechamento = 0;

    public boolean addParticipante(Usuario participante) {
        if (this.participantes == null) {
            this.participantes = new ArrayList<>();
        }
        if (this.limiteParticipantes == null) {
            this.limiteParticipantes = 5;
        }
        if (this.participantes.size() >= this.limiteParticipantes) {
            return false;
        }
        return this.participantes.add(participante);
    }
}

