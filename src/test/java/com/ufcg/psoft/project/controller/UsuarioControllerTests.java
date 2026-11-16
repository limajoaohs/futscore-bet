package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.project.dto.usuario.UsuarioPostDTO;
import com.ufcg.psoft.project.dto.usuario.UsuarioResponseDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.usuario.perfil.TipoPerfil;
import com.ufcg.psoft.project.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@DisplayName("US02 - Testes do Controlador de Usuários")
public class UsuarioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioPostDTO usuarioDTO;
    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = UsuarioPostDTO.builder()
                .nome("João Henrique")
                .email("joao.lima@ufcg.edu.br")
                .username("joaohsl")
                .endereco("Rua Aprígio Veloso, Bodocongó")
                .codigoAcesso("123456")
                .build();

        usuarioResponseDTO = UsuarioResponseDTO.builder()
                .id(1L)
                .nome("João Henrique")
                .email("joao.lima@ufcg.edu.br")
                .username("joaohsl")
                .perfil(TipoPerfil.PADRAO)
                .build();
    }

    @Test
    @DisplayName("US02 - Deve cadastrar usuário com sucesso quando os dados forem válidos")
    void deveCadastrarUsuarioComSucesso() throws Exception {
        Mockito.when(usuarioService.criar(Mockito.any(UsuarioPostDTO.class)))
                .thenReturn(usuarioResponseDTO);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("US02 - Deve rejeitar cadastro se o e-mail já estiver cadastrado no sistema")
    void deveFalharAoTentarCadastrarEmailDuplicado() throws Exception {
        Mockito.when(usuarioService.criar(Mockito.any(UsuarioPostDTO.class)))
                .thenThrow(new ProjectException("O email informado ja esta cadastrado."));

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isBadRequest());
    }
}
