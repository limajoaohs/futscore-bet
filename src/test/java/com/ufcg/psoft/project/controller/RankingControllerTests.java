package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.ranking.RankingResponseDTO;
import com.ufcg.psoft.project.service.ranking.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RankingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    private List<RankingResponseDTO> rankingSimulado;

    @BeforeEach
    void setUp() {
        RankingResponseDTO r1 = RankingResponseDTO.builder()
                .posicao(1)
                .usuarioNome("Charlie")
                .usuarioUsername("charlie_brown")
                .pontuacaoTotal(20)
                .quantidadePlacaresExatos(1)
                .quantidadeAcertosVencedor(1)
                .quantidadeAcertosEmpate(0)
                .build();

        RankingResponseDTO r2 = RankingResponseDTO.builder()
                .posicao(2)
                .usuarioNome("Alice")
                .usuarioUsername("alice_silva")
                .pontuacaoTotal(15)
                .quantidadePlacaresExatos(1)
                .quantidadeAcertosVencedor(1)
                .quantidadeAcertosEmpate(0)
                .build();

        rankingSimulado = Arrays.asList(r1, r2);
    }

    @Test
    @DisplayName("Deve retornar o ranking global com sucesso via API (HTTP 200)")
    void deveRetornarRankingGlobalAPI() throws Exception {
        when(rankingService.gerarRankingGlobal()).thenReturn(rankingSimulado);

        mockMvc.perform(get("/rankings/global")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].posicao").value(1))
                .andExpect(jsonPath("$[0].usuarioNome").value("Charlie"))
                .andExpect(jsonPath("$[0].pontuacaoTotal").value(20))
                .andExpect(jsonPath("$[1].posicao").value(2))
                .andExpect(jsonPath("$[1].usuarioNome").value("Alice"))
                .andExpect(jsonPath("$[1].pontuacaoTotal").value(15));

        verify(rankingService, times(1)).gerarRankingGlobal();
    }

    @Test
    @DisplayName("Deve retornar o ranking de um grupo com sucesso via API (HTTP 200)")
    void deveRetornarRankingGrupoAPI() throws Exception {
        Long grupoId = 100L;

        when(rankingService.gerarRankingDoGrupo(grupoId)).thenReturn(rankingSimulado);

        mockMvc.perform(get("/rankings/grupos/{grupoId}", grupoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].posicao").value(1))
                .andExpect(jsonPath("$[0].usuarioNome").value("Charlie"))
                .andExpect(jsonPath("$[0].pontuacaoTotal").value(20))
                .andExpect(jsonPath("$[1].posicao").value(2))
                .andExpect(jsonPath("$[1].usuarioNome").value("Alice"))
                .andExpect(jsonPath("$[1].pontuacaoTotal").value(15));

        verify(rankingService, times(1)).gerarRankingDoGrupo(grupoId);
    }
}