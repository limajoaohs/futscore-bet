package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.model.evento.EventoNotificacao;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import com.ufcg.psoft.project.service.notificacao.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class VerificadorEventosTempo {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Scheduled(cron = "0 * * * * *")
    public void dispararNotificacoesDeTempo() {

        LocalDateTime agora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<Partida> partidasFuturas = partidaRepository.findByDataHoraGreaterThanEqual(agora);

        for (Partida partida : partidasFuturas) {

            List<Grupo> grupos = grupoRepository.findByCampeonatoId(partida.getCampeonato().getId());

            for (Grupo grupo : grupos) {
                LocalDateTime horaAbertura = partida.getDataHora()
                        .minusMinutes(grupo.getMinutosParaAbertura())
                        .truncatedTo(ChronoUnit.MINUTES);

                LocalDateTime horaFechamento = partida.getDataHora()
                        .minusMinutes(grupo.getMinutosParaFechamento())
                        .truncatedTo(ChronoUnit.MINUTES);

                LocalDateTime horaInicio = partida.getDataHora()
                        .truncatedTo(ChronoUnit.MINUTES);

                if (agora.equals(horaAbertura)) {
                    notificarGrupo(grupo, EventoNotificacao.ABERTURA_PALPITES,
                            "Palpites abertos para a partida: " + partida.getTimeMandante() + " x " + partida.getTimeVisitante());
                }

                if (agora.equals(horaFechamento)) {
                    notificarGrupo(grupo, EventoNotificacao.FECHAMENTO_PALPITES,
                            "Palpites encerrados para a partida: " + partida.getTimeMandante() + " x " + partida.getTimeVisitante());
                }

                if (agora.equals(horaInicio)) {
                    notificarGrupo(grupo, EventoNotificacao.INICIO_PARTIDA,
                            "A partida " + partida.getTimeMandante() + " x " + partida.getTimeVisitante() + " começou!");
                }
            }
        }
    }

    private void notificarGrupo(Grupo grupo, EventoNotificacao tipo, String mensagem) {
        grupo.getParticipantes().forEach(participante ->
                notificacaoService.notificarUsuario(participante, tipo, mensagem)
        );
    }
}
