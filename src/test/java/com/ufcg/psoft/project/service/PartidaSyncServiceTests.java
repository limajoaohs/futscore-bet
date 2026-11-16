package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.client.ClienteFootballData;
import com.ufcg.psoft.project.dto.integration.*;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.partida.status.TipoStatus;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import com.ufcg.psoft.project.service.partida.PartidaSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidaSyncServiceTests {

    @InjectMocks
    private PartidaSyncService partidaSyncService;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private ClienteFootballData clienteFootballData;

    private Campeonato campeonato;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(clienteFootballData.configurado()).thenReturn(true);
        campeonato = Campeonato.builder()
                .id(1L)
                .nome("Premier League")
                .urlApi("https://api.football-data.org/v4/competitions/PL")
                .build();
    }

    @Test
    @DisplayName("Deve sincronizar partidas do campeonato com sucesso e atualizar os estados")
    void deveSincronizarPartidasComSucesso() {
        when(campeonatoRepository.findAll()).thenReturn(List.of(campeonato));

        MatchDTO matchDto = new MatchDTO();
        matchDto.setId(100L);
        matchDto.setStatus(TipoStatus.EM_ANDAMENTO);
        matchDto.setUtcDate("2026-07-17T20:00:00Z");

        TeamDTO homeTeam = new TeamDTO();
        homeTeam.setName("Arsenal");
        TeamDTO awayTeam = new TeamDTO();
        awayTeam.setName("Chelsea");
        matchDto.setHomeTeam(homeTeam);
        matchDto.setAwayTeam(awayTeam);

        ScoreDTO scoreDto = new ScoreDTO();
        FullTimeDTO fullTimeDto = new FullTimeDTO();
        fullTimeDto.setHome(2);
        fullTimeDto.setAway(1);
        scoreDto.setFullTime(fullTimeDto);
        matchDto.setScore(scoreDto);

        MatchResponseDTO matchResponse = new MatchResponseDTO();
        matchResponse.setMatches(List.of(matchDto));

        when(clienteFootballData.buscarPartidas(eq("https://api.football-data.org/v4/competitions/PL")))
                .thenReturn(matchResponse);

        when(partidaRepository.findByApiId(100L)).thenReturn(Optional.empty());

        partidaSyncService.sincronizarPartidas();

        verify(partidaRepository, times(1)).save(argThat(partida ->
                partida.getApiId().equals(100L) &&
                partida.getTimeMandante().equals("Arsenal") &&
                partida.getTimeVisitante().equals("Chelsea") &&
                partida.getStatus().getTipoStatus() == TipoStatus.EM_ANDAMENTO &&
                partida.getGolsMandante().equals(2) &&
                partida.getGolsVisitante().equals(1)
        ));
    }

    @Test
    @DisplayName("Deve tratar exceção da API sem interromper a execução")
    void deveTratarExcecaoAoBuscarPartidas() {
        when(campeonatoRepository.findAll()).thenReturn(List.of(campeonato));
        when(clienteFootballData.buscarPartidas(any())).thenThrow(new RuntimeException("API indisponível"));

        assertDoesNotThrow(() -> partidaSyncService.sincronizarPartidas());
        verify(partidaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve tratar resposta nula da API com segurança")
    void deveTratarRespostaNulaDaApi() {
        when(campeonatoRepository.findAll()).thenReturn(List.of(campeonato));
        when(clienteFootballData.buscarPartidas(any())).thenReturn(null);

        assertDoesNotThrow(() -> partidaSyncService.sincronizarPartidas());
        verify(partidaRepository, never()).save(any());
    }
}
