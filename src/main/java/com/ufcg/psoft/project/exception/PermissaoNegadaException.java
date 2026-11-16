package com.ufcg.psoft.project.exception;

public class PermissaoNegadaException extends ProjectException {
    public PermissaoNegadaException() {
        super("Acesso negado: Apenas o administrador unico pode realizar esta operacao.");
    }
}
