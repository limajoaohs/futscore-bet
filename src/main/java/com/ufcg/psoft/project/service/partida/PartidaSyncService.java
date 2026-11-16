package com.ufcg.psoft.project.service.partida;

import com.ufcg.psoft.project.client.ClienteFootballData;
import com.ufcg.psoft.project.dto.integration.MatchResponseDTO;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.partida.status.Status;
import com.ufcg.psoft.project.model.partida.status.TipoStatus;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PartidaSyncService {

    private final int TIMER = 300000;

    @Autowired
    private ClienteFootballData clienteFootballData;

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @Scheduled(fixedRate = TIMER)
    public void sincronizarPartidas() {
        if (!clienteFootballData.configurado()) return;
        List<Campeonato> campeonatos = campeonatoRepository.findAll();

        for (Campeonato campeonato : campeonatos) {
            try {
                MatchResponseDTO response = clienteFootballData.buscarPartidas(campeonato.getUrlApi());

                if (response != null && response.getMatches() != null) {
                    response.getMatches().forEach(matchDto -> {
                        Partida partida = partidaRepository.findByApiId(matchDto.getId())
                                .orElseGet(() -> Partida.builder()
                                        .campeonato(campeonato)
                                        .apiId(matchDto.getId())
                                        .build());

                        Status statusAnterior = partida.getStatus();
                        Integer golsMandanteAnterior = partida.getGolsMandante();
                        Integer golsVisitanteAnterior = partida.getGolsVisitante();

                        partida.setTimeMandante(matchDto.getHomeTeam() != null ? matchDto.getHomeTeam().getName() : "Unknown");
                        partida.setTimeVisitante(matchDto.getAwayTeam() != null ? matchDto.getAwayTeam().getName() : "Unknown");

                        if (matchDto.getStatus() != null) {
                            partida.setStatus(matchDto.getStatus().toEntity());
                        }

                        if (matchDto.getUtcDate() != null) {
                            java.time.Instant instant = java.time.Instant.parse(matchDto.getUtcDate());
                            partida.setDataHora(java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault()));
                        }

                        if (matchDto.getScore() != null && matchDto.getScore().getFullTime() != null) {
                            partida.setGolsMandante(matchDto.getScore().getFullTime().getHome());
                            partida.setGolsVisitante(matchDto.getScore().getFullTime().getAway());
                        }

                        Partida partidaSalva = partidaRepository.save(partida);
                    });
                }
            } catch (Exception e) {
                System.err.println("Erro ao sincronizar partidas do campeonato " + campeonato.getId() + ": " + e.getMessage());
            }
        }
    }
}