package com.ufcg.psoft.project.model.grupo;

import com.ufcg.psoft.project.model.usuario.Usuario;

public class CriterioMaiorPlacaresExatos implements CriterioDesempate {

    @Override
    public String getNome() {
        return "MAIOR_PLACAR_EXATO";
    }

    @Override
    public int comparar(Usuario u1, Usuario u2, Grupo grupo) {
        return 0;
    }
}
