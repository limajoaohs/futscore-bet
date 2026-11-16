package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.project.dto.grupo.GrupoRequestDTO;
import com.ufcg.psoft.project.dto.grupo.GrupoResponseDTO;
import com.ufcg.psoft.project.dto.grupo.CriteriosDesempateDTO;
import com.ufcg.psoft.project.model.grupo.Privacidade;
import com.ufcg.psoft.project.service.grupo.GrupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GrupoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GrupoService grupoService;

    private GrupoResponseDTO grupoResponseDTO;

    @BeforeEach
    void setUp() {
        grupoResponseDTO = GrupoResponseDTO.builder()
                .id(100L)
                .nome("Bolão da Galera")
                .descricao("Grupo para os amigos")
                .privacidade("PUBLICA")
                .limiteParticipantes(5)
                .campeonatoNome("Brasileirão")
                .organizadorNome("Criador Teste")
                .minutosParaAbertura(120)
                .minutosParaFechamento(30)
                .build();
    }

    @Test
    @DisplayName("Devem criar um grupo com sucesso via API e retornar HTTP 201")
    void deveCriarGrupoComSucessoAPI() throws Exception {

        GrupoRequestDTO requestDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, 10L, 120, 30);

        when(grupoService.criarGrupo(any(GrupoRequestDTO.class))).thenReturn(grupoResponseDTO);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.nome").value("Bolão da Galera"))
                .andExpect(jsonPath("$.privacidade").value("PUBLICA"))
                .andExpect(jsonPath("$.minutosParaAbertura").value(120))
                .andExpect(jsonPath("$.minutosParaFechamento").value(30));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o limite de participantes for negativo")
    void deveRetornarErroQuandoLimiteDeParticipantesForNegativo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, -5, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o limite de participantes for zero")
    void deveRetornarErroQuandoLimiteDeParticipantesForZero() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 0, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o nome for vazio")
    void deveRetornarErroQuandoNomeForVazio() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("", "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o nome for nulo")
    void deveRetornarErroQuandoNomeForNulo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO(null, "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando a descrição for vazia")
    void deveRetornarErroQuandoDescricaoForVazia() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "", Privacidade.PUBLICA, 5, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando a descrição for nula")
    void deveRetornarErroQuandoDescricaoForNula() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", null, Privacidade.PUBLICA, 5, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando a privacidade for vazia")
    void deveRetornarErroQuandoPrivacidadeForVazia() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", null, 5, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o limite de participantes for nulo")
    void deveRetornarErroQuandoLimiteDeParticipantesForNulo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, null, 1L, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o id do campeonato for nulo")
    void deveRetornarErroQuandoCampeonatoIdForNulo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 5, null, 10L, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando o id do criador for nulo")
    void deveRetornarErroQuandoCriadorIdForNulo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, null, 120, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando os minutos para abertura for negativo")
    void deveRetornarErroQuandoMinutosParaAberturaForNegativo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, 10L, -10, 30);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando os minutos para fechamento for negativo")
    void deveRetornarErroQuandoMinutosParaFechamentoForNegativo() throws Exception {
        GrupoRequestDTO requestInvalidoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, 10L, 120, -10);

        mockMvc.perform(post("/grupos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).criarGrupo(any());
    }

    @Test
    @DisplayName("Deve configurar criterios de desempate com sucesso via API e retornar HTTP 200")
    void deveConfigurarCriteriosDeDesempateComSucessoAPI() throws Exception {
        CriteriosDesempateDTO requestDTO = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        GrupoResponseDTO mockResponse = GrupoResponseDTO.builder()
                .id(100L)
                .nome("Bolão da Galera")
                .criteriosDesempate(requestDTO.getCriterios())
                .build();

        when(grupoService.configurarCriteriosDesempate(eq(100L), eq(10L), any(CriteriosDesempateDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/grupos/100/criterios-desempate")
                        .param("usuarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.criteriosDesempate[0]").value("MENOR_ERRO"))
                .andExpect(jsonPath("$.criteriosDesempate[1]").value("MAIOR_PLACAR_EXATO"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request ao tentar configurar criterios com lista nula")
    void deveRetornarErroQuandoCriteriosForNulo() throws Exception {
        CriteriosDesempateDTO requestInvalidoDTO = new CriteriosDesempateDTO(null);

        mockMvc.perform(put("/grupos/100/criterios-desempate")
                        .param("usuarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalidoDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).configurarCriteriosDesempate(any(), any(), any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request ao tentar configurar sem informacao do organizador")
    void deveRetornarErroQuandoOrganizadorIdAusente() throws Exception {
        CriteriosDesempateDTO requestDTO = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        mockMvc.perform(put("/grupos/100/criterios-desempate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).configurarCriteriosDesempate(any(), any(), any());
    }

    @Test
    @DisplayName("Deve retornar HTTP 403 Forbidden se usuario nao for o organizador")
    void deveRetornarErro403QuandoNaoForOrganizador() throws Exception {
        CriteriosDesempateDTO requestDTO = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        when(grupoService.configurarCriteriosDesempate(eq(100L), eq(11L), any(CriteriosDesempateDTO.class)))
                .thenThrow(new com.ufcg.psoft.project.exception.AcessoNegadoException("Apenas o organizador do grupo pode alterar os critérios de desempate!"));

        mockMvc.perform(put("/grupos/100/criterios-desempate")
                        .param("usuarioId", "11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }
}