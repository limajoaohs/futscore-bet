package com.ufcg.psoft.project.model.usuario.perfil;

public class PerfilPadrao implements PerfilUsuario {

    @Override
    public TipoPerfil getTipoPerfil(){
        return TipoPerfil.PADRAO;
    }

    @Override
    public boolean isAdmin() { return false; }
}
