package com.ufcg.psoft.project.model.palpite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "palpite", uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "partida_id", "grupo_id"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Palpite {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("golsMandante")
    @Column(nullable = false)
    private Integer golsMandante;

    @JsonProperty("golsVisitante")
    @Column(nullable = false)
    private Integer golsVisitante;

    @JsonProperty("usuario")
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @JsonProperty("partida")
    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @JsonProperty("grupo")
    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @JsonProperty("pontosObtidos")
    @Column(columnDefinition = "integer default 0")
    private Integer pontosObtidos;

    @JsonProperty("acertouPlacarExato")
    @Column(columnDefinition = "boolean default false")
    private boolean acertouPlacarExato;

    @JsonProperty("acertouVencedor")
    @Column(columnDefinition = "boolean default false")
    private boolean acertouVencedor;

    @JsonProperty("acertouEmpate")
    @Column(columnDefinition = "boolean default false")
    private boolean acertouEmpate;
}