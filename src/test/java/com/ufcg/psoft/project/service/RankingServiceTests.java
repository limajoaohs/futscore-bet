package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.ranking.RankingResponseDTO;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.palpite.Palpite;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.PalpiteRepository;
import com.ufcg.psoft.project.service.ranking.RankingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTests {

    @Mock
    private PalpiteRepository palpiteRepository;

    @InjectMocks
    private RankingServiceImpl rankingService;

    private Usuario alice;
    private Usuario bob;
    private Usuario charlie;
    private Grupo grupo;
    private List<Palpite> palpitesSimulados;

    @BeforeEach
    void setUp() {
        alice = Usuario.builder().id(1L).nome("Alice").username("alice_silva").build();
        bob = Usuario.builder().id(2L).nome("Bob").username("bob_santos").build();
        charlie = Usuario.builder().id(3L).nome("Charlie").username("charlie_brown").build();

        grupo = Grupo.builder().id(100L).nome("Grupo da Firma").build();

        Palpite p1 = Palpite.builder().id(1L).usuario(alice).grupo(grupo)
                .pontosObtidos(10).acertouPlacarExato(true).acertouVencedor(true).acertouEmpate(false).build();

        Palpite p2 = Palpite.builder().id(2L).usuario(alice).grupo(grupo)
                .pontosObtidos(5).acertouPlacarExato(false).acertouVencedor(true).acertouEmpate(false).build();

        Palpite p3 = Palpite.builder().id(3L).usuario(bob).grupo(grupo)
                .pontosObtidos(15).acertouPlacarExato(false).acertouVencedor(true).acertouEmpate(false).build();

        Palpite p4 = Palpite.builder().id(4L).usuario(charlie).grupo(grupo)
                .pontosObtidos(20).acertouPlacarExato(true).acertouVencedor(true).acertouEmpate(false).build();

        Palpite p5 = Palpite.builder().id(5L).usuario(bob).grupo(grupo)
                .pontosObtidos(null).acertouPlacarExato(false).acertouVencedor(false).acertouEmpate(false).build();

        palpitesSimulados = Arrays.asList(p1, p2, p3, p4, p5);
    }

    @Test
    @DisplayName("Deve gerar ranking global com sucesso, aplicando agregações e critérios de desempate")
    void deveGerarRankingGlobalComSucesso() {
        when(palpiteRepository.findAll()).thenReturn(palpitesSimulados);

        List<RankingResponseDTO> ranking = rankingService.gerarRankingGlobal();

        assertNotNull(ranking);
        assertEquals(3, ranking.size(), "Deve conter 3 usuários consolidados (o palpite null de Bob é ignorado)");

        assertEquals(1, ranking.get(0).getPosicao());
        assertEquals("Charlie", ranking.get(0).getUsuarioNome());
        assertEquals(20, ranking.get(0).getPontuacaoTotal());

        assertEquals(2, ranking.get(1).getPosicao());
        assertEquals("Alice", ranking.get(1).getUsuarioNome());
        assertEquals(15, ranking.get(1).getPontuacaoTotal());
        assertEquals(1, ranking.get(1).getQuantidadePlacaresExatos());

        assertEquals(3, ranking.get(2).getPosicao());
        assertEquals("Bob", ranking.get(2).getUsuarioNome());
        assertEquals(15, ranking.get(2).getPontuacaoTotal());
        assertEquals(0, ranking.get(2).getQuantidadePlacaresExatos());

        verify(palpiteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve gerar ranking do grupo com sucesso")
    void deveGerarRankingDoGrupoComSucesso() {
        when(palpiteRepository.findByGrupoId(100L)).thenReturn(palpitesSimulados);

        List<RankingResponseDTO> ranking = rankingService.gerarRankingDoGrupo(100L);

        assertNotNull(ranking);
        assertEquals(3, ranking.size());

        assertEquals("Charlie", ranking.get(0).getUsuarioNome());
        assertEquals("Alice", ranking.get(1).getUsuarioNome());
        assertEquals("Bob", ranking.get(2).getUsuarioNome());

        verify(palpiteRepository, times(1)).findByGrupoId(100L);
    }

    @Test
    @DisplayName("Deve ignorar palpites com pontos null sem lançar exceção")
    void deveIgnorarPalpitesComPontosNull() {
        Palpite palpiteNaoAvaliado1 = Palpite.builder().usuario(alice).pontosObtidos(null).build();
        Palpite palpiteNaoAvaliado2 = Palpite.builder().usuario(bob).pontosObtidos(null).build();

        when(palpiteRepository.findAll()).thenReturn(Arrays.asList(palpiteNaoAvaliado1, palpiteNaoAvaliado2));

        List<RankingResponseDTO> ranking = rankingService.gerarRankingGlobal();

        assertNotNull(ranking);
        assertTrue(ranking.isEmpty(), "O ranking deve ser vazio pois nenhum palpite foi avaliado ainda.");
    }

    @Test
    @DisplayName("Deve retornar lista vazia se não houver nenhum palpite no banco/grupo")
    void deveRetornarListaVaziaSeNaoHouverPalpites() {
        when(palpiteRepository.findByGrupoId(99L)).thenReturn(Collections.emptyList());

        List<RankingResponseDTO> ranking = rankingService.gerarRankingDoGrupo(99L);

        assertNotNull(ranking);
        assertTrue(ranking.isEmpty());
        verify(palpiteRepository, times(1)).findByGrupoId(99L);
    }

    @Test
    @DisplayName("Deve consolidar múltiplos palpites de um mesmo usuário corretamente")
    void deveConsolidarMultiplosPalpites() {
        Palpite p1 = Palpite.builder().usuario(alice).pontosObtidos(10).acertouVencedor(true).acertouPlacarExato(true).build();
        Palpite p2 = Palpite.builder().usuario(alice).pontosObtidos(5).acertouVencedor(true).acertouEmpate(false).build();
        Palpite p3 = Palpite.builder().usuario(alice).pontosObtidos(3).acertouEmpate(true).build();

        when(palpiteRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        List<RankingResponseDTO> ranking = rankingService.gerarRankingGlobal();

        assertEquals(1, ranking.size(), "Deve agrupar tudo em um único DTO para Alice");

        RankingResponseDTO aliceRanking = ranking.get(0);
        assertEquals(18, aliceRanking.getPontuacaoTotal(), "Deve somar 10 + 5 + 3");
        assertEquals(2, aliceRanking.getQuantidadeAcertosVencedor());
        assertEquals(1, aliceRanking.getQuantidadeAcertosEmpate());
        assertEquals(1, aliceRanking.getQuantidadePlacaresExatos());
    }
}