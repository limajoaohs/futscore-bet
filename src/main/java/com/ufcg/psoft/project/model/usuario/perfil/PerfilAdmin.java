package com.ufcg.psoft.project.model.usuario.perfil;

public class PerfilAdmin implements PerfilUsuario {

    @Override
    public TipoPerfil getTipoPerfil() { return TipoPerfil.ADMIN; }

    @Override
    public boolean isAdmin(){
        return true;
    }
}
