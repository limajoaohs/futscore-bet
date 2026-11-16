package com.ufcg.psoft.project.service.notificacao;

import com.ufcg.psoft.project.model.evento.EventoNotificacao;
import com.ufcg.psoft.project.model.usuario.Usuario;

public interface NotificacaoService {
    void notificarUsuario(Usuario usuario, EventoNotificacao evento, String mensagem);
}