package com.ufcg.psoft.project.config;

import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/** Popula partidas de exemplo para o site funcionar sem token da Football-Data. */
@Component
@ConditionalOnProperty(name = "app.demo-seed", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    private static final String[][] JOGOS = {
            {"Flamengo", "Palmeiras"}, {"Corinthians", "Sao Paulo"}, {"Gremio", "Internacional"},
            {"Atletico-MG", "Cruzeiro"}, {"Fluminense", "Botafogo"}, {"Santos", "Vasco"},
            {"Bahia", "Fortaleza"}, {"Athletico-PR", "Bragantino"}, {"Vitoria", "Sport"},
            {"Ceara", "Juventude"}, {"Real Madrid", "Barcelona"}, {"Manchester City", "Liverpool"},
    };

    private final CampeonatoRepository campeonatoRepository;
    private final PartidaRepository partidaRepository;

    public DemoDataSeeder(CampeonatoRepository campeonatoRepository, PartidaRepository partidaRepository) {
        this.campeonatoRepository = campeonatoRepository;
        this.partidaRepository = partidaRepository;
    }

    @Override
    public void run(String... args) {
        if (partidaRepository.count() > 0) return;
        Campeonato camp = campeonatoRepository.save(Campeonato.builder()
                .nome("Rodada Demo").ano(String.valueOf(LocalDateTime.now().getYear()))
                .urlApi("demo").apiId(-1L).build());
        LocalDateTime base = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2);
        for (int i = 0; i < JOGOS.length; i++) {
            partidaRepository.save(Partida.builder()
                    .apiId(-1000L - i).campeonato(camp)
                    .timeMandante(JOGOS[i][0]).timeVisitante(JOGOS[i][1])
                    .dataHora(base.plusHours(i * 5L)).build());
        }
    }
}
