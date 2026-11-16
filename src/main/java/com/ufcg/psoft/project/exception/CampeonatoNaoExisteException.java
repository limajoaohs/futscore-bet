package com.ufcg.psoft.project.exception;

public class CampeonatoNaoExisteException extends RuntimeException {
    public CampeonatoNaoExisteException(Long id) {
        super("Campeonato não encontrado com o ID: " + id);
    }
}
