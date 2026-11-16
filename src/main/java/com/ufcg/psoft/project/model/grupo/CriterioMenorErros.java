package com.ufcg.psoft.project.model.grupo;

import com.ufcg.psoft.project.model.usuario.Usuario;

public class CriterioMenorErros implements CriterioDesempate {

    @Override
    public String getNome() {
        return "MENOR_ERRO";
    }

    @Override
    public int comparar(Usuario u1, Usuario u2, Grupo grupo) {
        return 0;
    }
}
