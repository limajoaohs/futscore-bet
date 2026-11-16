package com.ufcg.psoft.project.model.usuario.perfil;

public enum TipoPerfil {

    ADMIN, PADRAO, PREMIUM;

    public PerfilUsuario toEntity() {
        return switch (this) {
            case ADMIN -> new PerfilAdmin();
            case PREMIUM -> new PerfilPremium();
            case PADRAO -> new PerfilPadrao();
        };
    }
}
