package com.ufcg.psoft.project.model.grupo;

import com.ufcg.psoft.project.model.usuario.Usuario;

public interface CriterioDesempate {
    String getNome();
    int comparar(Usuario u1, Usuario u2, Grupo grupo);
}
