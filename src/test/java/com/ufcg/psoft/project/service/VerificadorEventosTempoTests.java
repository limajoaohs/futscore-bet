package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.evento.EventoNotificacao;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import com.ufcg.psoft.project.service.notificacao.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificadorEventosTempoTests {

    @InjectMocks
    private VerificadorEventosTempo verificador;

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private NotificacaoService notificacaoService;

    private Partida partida;
    private Grupo grupo;
    private Usuario usuario;
    private LocalDateTime agora;

    @BeforeEach
    void setUp() {
        agora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        Campeonato campeonato = new Campeonato();
        campeonato.setId(1L);

        partida = new Partida();
        partida.setId(100L);
        partida.setCampeonato(campeonato);
        partida.setTimeMandante("Flamengo");
        partida.setTimeVisitante("Vasco");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");

        grupo = new Grupo();
        grupo.setId(50L);
        grupo.setMinutosParaAbertura(120);
        grupo.setMinutosParaFechamento(30);
        grupo.getParticipantes().add(usuario);

        lenient().when(partidaRepository.findByDataHoraGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(List.of(partida));

        lenient().when(grupoRepository.findByCampeonatoId(1L))
                .thenReturn(List.of(grupo));
    }

    @Test
    @DisplayName("Deve notificar abertura de palpites na hora exata")
    void deveNotificarAberturaPalpites() {
        partida.setDataHora(agora.plusMinutes(120));
        verificador.dispararNotificacoesDeTempo();

        verify(notificacaoService, times(1)).notificarUsuario(
                eq(usuario),
                eq(EventoNotificacao.ABERTURA_PALPITES),
                eq("Palpites abertos para a partida: Flamengo x Vasco")
        );
    }

    @Test
    @DisplayName("Deve notificar fechamento de palpites na hora exata")
    void deveNotificarFechamentoPalpites() {
        partida.setDataHora(agora.plusMinutes(30));
        verificador.dispararNotificacoesDeTempo();

        verify(notificacaoService, times(1)).notificarUsuario(
                eq(usuario),
                eq(EventoNotificacao.FECHAMENTO_PALPITES),
                eq("Palpites encerrados para a partida: Flamengo x Vasco")
        );
    }

    @Test
    @DisplayName("Deve notificar início da partida na hora exata")
    void deveNotificarInicioPartida() {
        partida.setDataHora(agora);
        verificador.dispararNotificacoesDeTempo();

        verify(notificacaoService, times(1)).notificarUsuario(
                eq(usuario),
                eq(EventoNotificacao.INICIO_PARTIDA),
                eq("A partida Flamengo x Vasco começou!")
        );
    }

    @Test
    @DisplayName("Não deve notificar nada quando ainda faltar tempo para a abertura")
    void naoDeveNotificarAntesDaAbertura() {
        partida.setDataHora(agora.plusMinutes(180));
        verificador.dispararNotificacoesDeTempo();
        verify(notificacaoService, never()).notificarUsuario(any(), any(), anyString());
    }

    @Test
    @DisplayName("Não deve notificar nada quando estiver no meio do período de palpites")
    void naoDeveNotificarDuranteOPeriodoDePalpites() {
        partida.setDataHora(agora.plusMinutes(60));
        verificador.dispararNotificacoesDeTempo();
        verify(notificacaoService, never()).notificarUsuario(any(), any(), anyString());
    }

    @Test
    @DisplayName("Não deve notificar nada para partidas que já iniciaram no passado")
    void naoDeveNotificarPartidasQueJaPassaram() {
        partida.setDataHora(agora.minusMinutes(15));
        verificador.dispararNotificacoesDeTempo();
        verify(notificacaoService, never()).notificarUsuario(any(), any(), anyString());
    }

    @Test
    @DisplayName("Não deve fazer nada quando não houver partidas futuras no banco")
    void naoDeveFazerNadaQuandoListaDePartidasEstiverVazia() {
        when(partidaRepository.findByDataHoraGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(List.of());
        verificador.dispararNotificacoesDeTempo();

        verify(notificacaoService, never()).notificarUsuario(any(), any(), anyString());
        verify(grupoRepository, never()).findByCampeonatoId(anyLong());
    }

    @Test
    @DisplayName("Deve respeitar as regras individuais de tempo de cada grupo isoladamente")
    void deveRespeitarRegrasIndividuaisDeCadaGrupo() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria");

        Grupo grupo2 = new Grupo();
        grupo2.setId(51L);
        grupo2.setMinutosParaAbertura(180);
        grupo2.getParticipantes().add(usuario2);

        when(grupoRepository.findByCampeonatoId(1L))
                .thenReturn(List.of(grupo, grupo2));

        partida.setDataHora(agora.plusMinutes(120));

        verificador.dispararNotificacoesDeTempo();

        verify(notificacaoService, times(1)).notificarUsuario(
                eq(usuario),
                eq(EventoNotificacao.ABERTURA_PALPITES),
                eq("Palpites abertos para a partida: Flamengo x Vasco")
        );

        verify(notificacaoService, never()).notificarUsuario(
                eq(usuario2),
                any(),
                anyString()
        );
    }
}