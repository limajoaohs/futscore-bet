package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.project.dto.palpite.PalpiteRequestDTO;
import com.ufcg.psoft.project.dto.palpite.PalpiteResponseDTO;
import com.ufcg.psoft.project.service.palpite.PalpiteService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PalpiteControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PalpiteService palpiteService;

    private PalpiteResponseDTO palpiteResponseDTO;

    @BeforeEach
    void setUp() {
        palpiteResponseDTO = PalpiteResponseDTO.builder()
                .id(999L)
                .golsMandante(2)
                .golsVisitante(1)
                .usuarioId(1L)
                .usuarioUsername("usuario_teste")
                .partidaId(100L)
                .partidaTimeMandante("Flamengo")
                .partidaTimeVisitante("Vasco")
                .grupoId(50L)
                .grupoNome("Grupo da Família")
                .build();
    }

    @Test
    @DisplayName("Deve registrar um palpite com sucesso via API e retornar HTTP 201")
    void deveRegistrarPalpiteComSucessoAPI() throws Exception {
        PalpiteRequestDTO requestDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        when(palpiteService.registrarPalpite(any(PalpiteRequestDTO.class))).thenReturn(palpiteResponseDTO);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(999L))
                .andExpect(jsonPath("$.golsMandante").value(2))
                .andExpect(jsonPath("$.golsVisitante").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$.usuarioUsername").value("usuario_teste"))
                .andExpect(jsonPath("$.partidaId").value(100L))
                .andExpect(jsonPath("$.partidaTimeMandante").value("Flamengo"))
                .andExpect(jsonPath("$.partidaTimeVisitante").value("Vasco"))
                .andExpect(jsonPath("$.grupoId").value(50L))
                .andExpect(jsonPath("$.grupoNome").value("Grupo da Família"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando gols do mandante for nulo")
    void deveRetornarErroQuandoGolsMandanteForNulo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(1L, 100L, 50L, null, 1);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("A quantidade de gols do mandante é obrigatória"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o número de gols do mandante for negativo")
    void deveRetornarErroQuandoNumeroDeGolsMandanteForNegativo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(1L, 100L, 50L, -1, 1);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("A quantidade de gols do mandante não pode ser negativa"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando gols do visitante for nulo")
    void deveRetornarErroQuandoGolsVisitanteForNulo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, null);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("A quantidade de gols do visitante é obrigatória"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o número de gols do visitante for negativo")
    void deveRetornarErroQuandoNumeroDeGolsVisitanteForNegativo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, -1);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("A quantidade de gols do visitante não pode ser negativa"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o usuarioId for nulo")
    void deveRetornarErroQuandoUsuarioIdForNulo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(null, 100L, 50L, 2, 1);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("O ID do usuário é obrigatório"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o partidaId for nulo")
    void deveRetornarErroQuandoPartidaIdForNulo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(1L, null, 50L, 2, 1);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("O ID da partida é obrigatório"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o grupoId for nulo")
    void deveRetornarErroQuandoGrupoIdForNulo() throws Exception {
        PalpiteRequestDTO requestInvalidoDTO = new PalpiteRequestDTO(1L, 100L, null, 2, 1);

        mockMvc.perform(post("/palpites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("O ID do grupo é obrigatório"));

        verify(palpiteService, never()).registrarPalpite(any());
    }

    @Test
    @DisplayName("Deve editar um palpite com sucesso via API e retornar HTTP 200")
    void deveEditarPalpiteComSucessoAPI() throws Exception {
        Long idPalpite = 999L;

        PalpiteRequestDTO requestDTO = new PalpiteRequestDTO(1L, 100L, 50L, 3, 2);

        palpiteResponseDTO.setGolsMandante(3);
        palpiteResponseDTO.setGolsVisitante(2);

        when(palpiteService.editarPalpite(eq(idPalpite), any(PalpiteRequestDTO.class)))
                .thenReturn(palpiteResponseDTO);

        mockMvc.perform(put("/palpites/{id}", idPalpite)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(999L))
                .andExpect(jsonPath("$.golsMandante").value(3))
                .andExpect(jsonPath("$.golsVisitante").value(2))
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$.grupoId").value(50L));
    }
}
