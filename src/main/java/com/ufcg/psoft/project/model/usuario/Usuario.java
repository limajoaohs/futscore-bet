package com.ufcg.psoft.project.model.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.usuario.perfil.PerfilUsuarioConverter;
import com.ufcg.psoft.project.model.usuario.perfil.PerfilPadrao;
import com.ufcg.psoft.project.model.usuario.perfil.PerfilUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("email")
    @Column(unique = true)
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("codigoAcesso")
    @Column(name = "codigo", nullable = false)
    private String codigoAcesso;

    @JsonProperty("endereco")
    @Column(nullable = true)
    private String endereco;

    @JsonProperty("perfil")
    @Column(nullable = false)
    @Convert(converter = PerfilUsuarioConverter.class)
    @Builder.Default
    private PerfilUsuario perfil = new PerfilPadrao();

    // creditos virtuais (sem dinheiro real); novo usuario recebe bonus de boas-vindas
    @JsonProperty("saldo")
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private java.math.BigDecimal saldo = new java.math.BigDecimal("1000.00");
}
