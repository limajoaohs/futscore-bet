package com.ufcg.psoft.project.model.partida.status;

public class EmAndamento implements Status {

    @Override
    public TipoStatus getTipoStatus() {
        return TipoStatus.EM_ANDAMENTO;
    }

    @Override
    public boolean isAberto() {
        return false;
    }
}
