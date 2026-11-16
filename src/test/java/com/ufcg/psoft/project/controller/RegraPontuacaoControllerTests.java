package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.project.dto.RegraPontuacaoRequestDTO;
import com.ufcg.psoft.project.model.RegraPontuacao;
import com.ufcg.psoft.project.service.regrapontuacao.RegraPontuacaoService;
import com.ufcg.psoft.project.exception.AcessoNegadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
public class RegraPontuacaoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegraPontuacaoService regraPontuacaoService;

    private RegraPontuacao regraSalva;

    @BeforeEach
    void setUp() {
        regraSalva = RegraPontuacao.builder()
                .id(1L)
                .pontosAcertoVencedor(3)
                .pontosAcertoEmpate(1)
                .pontosPlacarExato(5)
                .pontosBonusRodada(10)
                .pontosBonusMataMata(20)
                .build();
    }

    @Test
    @DisplayName("Deve configurar regras de pontuação com sucesso via API e retornar HTTP 200")
    void deveConfigurarRegrasComSucessoAPI() throws Exception {
        RegraPontuacaoRequestDTO requestDTO = new RegraPontuacaoRequestDTO(3, 1, 5, 10, 20);

        when(regraPontuacaoService.configurarRegraPontuacao(eq(100L), eq(10L), any(RegraPontuacaoRequestDTO.class)))
                .thenReturn(regraSalva);

        mockMvc.perform(put("/grupos/100/regras-pontuacao")
                        .param("usuarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pontosAcertoVencedor").value(3))
                .andExpect(jsonPath("$.pontosAcertoEmpate").value(1))
                .andExpect(jsonPath("$.pontosPlacarExato").value(5))
                .andExpect(jsonPath("$.pontosBonusRodada").value(10))
                .andExpect(jsonPath("$.pontosBonusMataMata").value(20));

        verify(regraPontuacaoService, times(1))
                .configurarRegraPontuacao(eq(100L), eq(10L), any(RegraPontuacaoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar HTTP 403 Forbidden quando o usuário não for o organizador")
    void deveRetornarForbiddenQuandoNaoForOrganizador() throws Exception {
        RegraPontuacaoRequestDTO requestDTO = new RegraPontuacaoRequestDTO(3, 1, 5, 10, 20);

        when(regraPontuacaoService.configurarRegraPontuacao(eq(100L), eq(20L), any(RegraPontuacaoRequestDTO.class)))
                .thenThrow(new AcessoNegadoException("Apenas o organizador do grupo pode alterar as regras de pontuação!"));

        mockMvc.perform(put("/grupos/100/regras-pontuacao")
                        .param("usuarioId", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("proverDTOsInvalidos")
    @DisplayName("Deve retornar HTTP 400 Bad Request para DTOs com valores inválidos")
    void deveRetornarErroParaValoresInvalidos(RegraPontuacaoRequestDTO dtoInvalido) throws Exception {
        mockMvc.perform(put("/grupos/100/regras-pontuacao")
                        .param("usuarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(regraPontuacaoService, never()).configurarRegraPontuacao(any(), any(), any());
    }

    private static Stream<RegraPontuacaoRequestDTO> proverDTOsInvalidos() {
        return Stream.of(
                new RegraPontuacaoRequestDTO(-1, 1, 5, 10, 20),
                new RegraPontuacaoRequestDTO(null, 1, 5, 10, 20),
                new RegraPontuacaoRequestDTO(3, -1, 5, 10, 20),
                new RegraPontuacaoRequestDTO(3, null, 5, 10, 20),
                new RegraPontuacaoRequestDTO(3, 1, -5, 10, 20),
                new RegraPontuacaoRequestDTO(3, 1, null, 10, 20),
                new RegraPontuacaoRequestDTO(3, 1, 5, -10, 20),
                new RegraPontuacaoRequestDTO(3, 1, 5, null, 20),
                new RegraPontuacaoRequestDTO(3, 1, 5, 10, -20),
                new RegraPontuacaoRequestDTO(3, 1, 5, 10, null)
        );
    }
}
