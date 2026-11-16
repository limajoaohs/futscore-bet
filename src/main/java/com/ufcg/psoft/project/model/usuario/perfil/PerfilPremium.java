package com.ufcg.psoft.project.model.usuario.perfil;

public class PerfilPremium implements PerfilUsuario {

    @Override
    public TipoPerfil getTipoPerfil(){
        return TipoPerfil.PREMIUM;
    }

    @Override
    public boolean isAdmin(){
        return false;
    }
}
