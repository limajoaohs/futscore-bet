package com.ufcg.psoft.project.model.partida.status;

public class Aberto implements Status {

    @Override
    public TipoStatus getTipoStatus() {
        return TipoStatus.ABERTO;
    }

    @Override
    public boolean isAberto() {
        return true;
    }
}
