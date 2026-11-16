package com.ufcg.psoft.project.model.aposta;

import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aposta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aposta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private Partida partida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Palpite1X2 palpite;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    // odd travada no momento da aposta
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal odd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusAposta status = StatusAposta.PENDENTE;

    @Builder.Default
    private LocalDateTime criadaEm = LocalDateTime.now();

    public BigDecimal getRetornoPotencial() {
        return valor.multiply(odd).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
