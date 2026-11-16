package com.ufcg.psoft.project.model.convite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convite")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("grupo")
    @ManyToOne(optional = false)
    private Grupo grupo;

    @JsonProperty("organizador")
    @ManyToOne(optional = false)
    private Usuario organizador;

    @JsonProperty
    @ManyToOne(optional = false)
    private Usuario convidado;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConvite status;
}
