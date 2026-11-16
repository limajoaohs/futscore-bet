package com.ufcg.psoft.project.service.pontuacao;

import com.ufcg.psoft.project.model.partida.Partida;

public interface PontuacaoService {
    void calcularPontuacoesDaPartida(Partida partida);
}