package com.ufcg.psoft.project.model.partida.status;

public class Cancelado implements Status {

    @Override
    public TipoStatus getTipoStatus() {
        return TipoStatus.CANCELADO;
    }

    @Override
    public boolean isAberto() {
        return false;
    }
}
