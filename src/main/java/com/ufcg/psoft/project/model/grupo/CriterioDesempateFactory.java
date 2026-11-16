package com.ufcg.psoft.project.model.grupo;

import com.ufcg.psoft.project.exception.ProjectException;
import org.springframework.stereotype.Component;

@Component
public class CriterioDesempateFactory {

    public CriterioDesempate obterCriterio(String nome) {
        if (nome == null) {
            throw new ProjectException("Nome do critério não pode ser nulo");
        }
        return switch (nome.trim().toUpperCase()) {
            case "MAIOR_PLACAR_EXATO" -> new CriterioMaiorPlacaresExatos();
            case "MENOR_ERRO" -> new CriterioMenorErros();
            case "MAIOR_ACERTO_VENCEDOR" -> new CriterioMaiorAcertosVencedor();
            case "MAIOR_ACERTO_EMPATE" -> new CriterioMaiorAcertosEmpate();
            default -> throw new ProjectException("Critério de desempate inválido: " + nome);
        };
    }
}
