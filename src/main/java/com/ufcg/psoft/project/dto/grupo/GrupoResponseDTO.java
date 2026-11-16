package com.ufcg.psoft.project.dto.grupo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.dto.usuario.UsuarioResponseDTO;
import com.ufcg.psoft.project.model.usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("campeonato")
    private String campeonatoNome;

    @JsonProperty("organizador")
    private String organizadorNome;

    @JsonProperty("privacidade")
    private String privacidade;

    @JsonProperty("limiteParticipantes")
    private Integer limiteParticipantes;

    @JsonProperty("participantes")
    private List<UsuarioResponseDTO> participantes;

    @JsonProperty("minutosParaAbertura")
    private Integer minutosParaAbertura;

    @JsonProperty("minutosParaFechamento")
    private Integer minutosParaFechamento;

    @JsonProperty("criteriosDesempate")
    private List<String> criteriosDesempate;
}