package com.ufcg.psoft.project.service.notificacao;

import com.ufcg.psoft.project.model.evento.EventoNotificacao;
import com.ufcg.psoft.project.model.usuario.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoServiceImpl.class);

    @Override
    public void notificarUsuario(Usuario usuario, EventoNotificacao evento, String mensagem) {
        String logMessage = String.format("[NOTIFICAÇÃO - %s] Para: %s (%s) -> %s",
                evento.name(), usuario.getNome(), usuario.getEmail(), mensagem);
        logger.info(logMessage);
    }
}