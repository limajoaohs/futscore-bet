package com.ufcg.psoft.project.model.partida.status;

public class Finalizado implements Status {

    @Override
    public TipoStatus getTipoStatus() {
        return TipoStatus.FINALIZADO;
    }

    @Override
    public boolean isAberto() {
        return false;
    }
}
