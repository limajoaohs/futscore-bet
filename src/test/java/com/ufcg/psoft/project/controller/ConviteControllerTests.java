package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.project.dto.convite.ConviteRequestDTO;
import com.ufcg.psoft.project.dto.convite.ConviteResponseDTO;
import com.ufcg.psoft.project.model.convite.StatusConvite;
import com.ufcg.psoft.project.service.convite.ConviteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConviteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConviteService conviteService;

    @Test
    public void testEnviarConviteEndpoint() throws Exception {
        ConviteRequestDTO requestDTO = ConviteRequestDTO.builder()
                .grupoId(1L)
                .organizadorId(1L)
                .convidadoId(2L)
                .build();

        ConviteResponseDTO responseDTO = ConviteResponseDTO.builder()
                .id(1L)
                .grupoId(1L)
                .organizadorId(1L)
                .convidadoId(2L)
                .status(StatusConvite.PENDENTE)
                .build();

        when(conviteService.enviarConvite(any(ConviteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/convites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testResponderConviteEndpoint() throws Exception {
        ConviteResponseDTO responseDTO = ConviteResponseDTO.builder()
                .id(1L)
                .grupoId(1L)
                .organizadorId(1L)
                .convidadoId(2L)
                .status(StatusConvite.ACEITO)
                .build();

        when(conviteService.responderConvite(1L, 2L, "654321", "ACEITAR")).thenReturn(responseDTO);

        mockMvc.perform(put("/convites/1/responder")
                        .param("usuarioId", "2")
                        .param("codigoAcesso", "654321")
                        .param("acao", "ACEITAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEITO"));
    }
}