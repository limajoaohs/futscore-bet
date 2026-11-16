package com.ufcg.psoft.project.model.partida.status;

public enum TipoStatus {

    ABERTO, EM_ANDAMENTO, FINALIZADO, CANCELADO;

    public Status toEntity() {
        return switch (this) {
            case ABERTO -> new Aberto();
            case EM_ANDAMENTO -> new EmAndamento();
            case FINALIZADO -> new Finalizado();
            case CANCELADO -> new Cancelado();
        };
    }
}
