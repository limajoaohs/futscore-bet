package com.ufcg.psoft.project.model.partida;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.partida.status.Aberto;
import com.ufcg.psoft.project.model.partida.status.EmAndamento;
import com.ufcg.psoft.project.model.partida.status.Status;
import com.ufcg.psoft.project.model.partida.status.StatusPartidaConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "partida")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partida {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("apiId")
    @Column(nullable = false, unique = true)
    private Long apiId;

    @ManyToOne
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;
    
    @JsonProperty("timeMandante")
    @Column(nullable = false)
    private String timeMandante;

    @JsonProperty("timeVisitante")
    @Column(nullable = false)
    private String timeVisitante;

    @JsonProperty("golsMandante")
    private Integer golsMandante;

    @JsonProperty("golsVisitante")
    private Integer golsVisitante;

    @JsonProperty("dataHora")
    private LocalDateTime dataHora;

    @JsonProperty("status")
    @Column(nullable = false)
    @Convert(converter = StatusPartidaConverter.class)
    @Builder.Default
    private Status status = new Aberto();

    /** Aceita apostas ate o apito inicial. */
    public boolean aceitaApostas() {
        return status.isAberto() && dataHora != null && LocalDateTime.now().isBefore(dataHora);
    }

    public com.ufcg.psoft.project.model.aposta.Palpite1X2 resultado() {
        int diff = Integer.compare(golsMandante, golsVisitante);
        return diff > 0 ? com.ufcg.psoft.project.model.aposta.Palpite1X2.CASA
                : diff < 0 ? com.ufcg.psoft.project.model.aposta.Palpite1X2.FORA
                : com.ufcg.psoft.project.model.aposta.Palpite1X2.EMPATE;
    }

    /**
     * Odd decimal com margem da casa de ~6%.
     * shortcut: forca dos times derivada do hash do nome (deterministica), trocar por feed de odds/modelo real quando houver.
     */
    public java.math.BigDecimal odd(com.ufcg.psoft.project.model.aposta.Palpite1X2 palpite) {
        double fc = 1 + Math.floorMod(timeMandante.hashCode(), 100) / 100.0 * 1.15; // fator mando de campo
        double ff = 1 + Math.floorMod(timeVisitante.hashCode(), 100) / 100.0;
        double pEmpate = 0.27;
        double pCasa = (1 - pEmpate) * fc / (fc + ff);
        double p = switch (palpite) {
            case CASA -> pCasa;
            case EMPATE -> pEmpate;
            case FORA -> 1 - pEmpate - pCasa;
        };
        double odd = Math.max(1.01, 1 / (p * 1.06));
        return java.math.BigDecimal.valueOf(odd).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public boolean periodoDePalpiteValido(int abertura, int fechamento) {
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(dataHora)) {
            this.status = new EmAndamento();
        }
        return status.isAberto()
                && agora.isAfter(dataHora.minusMinutes(abertura))
                && agora.isBefore(dataHora.minusMinutes(fechamento));
    }
}
