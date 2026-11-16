package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.project.dto.campeonato.CampeonatoRequestDTO;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.model.usuario.perfil.PerfilAdmin;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("US01 - Testes do Controlador de Campeonatos")
public class CampeonatoControllerTests {

    final String URI_CAMPEONATOS = "/campeonatos";

    @Autowired
    MockMvc driver;

    @Autowired
    CampeonatoRepository campeonatoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    CampeonatoRequestDTO campeonatoRequestDTO;
    Usuario admin;
    Usuario comum;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        campeonatoRepository.deleteAll();
        usuarioRepository.deleteAll();

        admin = Usuario.builder()
                .id(1L)
                .nome("adminNome")
                .email("admin@gmail.com")
                .username("adminUsername")
                .codigoAcesso("123456")
                .endereco("adminEndereco")
                .perfil(new PerfilAdmin())
                .build();

        comum = Usuario.builder()
                .id(2L)
                .nome("comumNome")
                .email("comum@gmail.com")
                .username("comumUsername")
                .codigoAcesso("123456")
                .endereco("comumEndereco")
                .build();

        admin = usuarioRepository.save(admin);
        comum = usuarioRepository.save(comum);

        campeonatoRequestDTO = new CampeonatoRequestDTO();
        campeonatoRequestDTO.setNome("Copa do Nordeste");
        campeonatoRequestDTO.setUrlApi("https://api.football-data.org/v4/competitions/CNE");
        campeonatoRequestDTO.setAno("2026");
    }

    @AfterEach
    void tearDown() {
        campeonatoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("US01 - Deve permitir cadastrar campeonato manualmente quando for administrador")
    void deveSalvarNoBancoQuandoAdministradorCadastrar() throws Exception {
        driver.perform(post(URI_CAMPEONATOS)
                .param("adminId", admin.getId().toString())
                .param("codigoAcesso", admin.getCodigoAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(campeonatoRequestDTO)))
                .andExpect(status().isCreated());
        assertEquals(1, campeonatoRepository.count());
        assertEquals("Copa do Nordeste", campeonatoRepository.findAll().get(0).getNome());
    }

    @Test
    @DisplayName("US01 - Deve barrar cadastro manual quando o usuario comum tentar")
    void deveBarrarNoBancoQuandoUsuarioComumTentarCadastrar() throws Exception {
        driver.perform(post(URI_CAMPEONATOS)
                .param("adminId", comum.getId().toString())
                .param("codigoAcesso", admin.getCodigoAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(campeonatoRequestDTO)))
                .andExpect(status().isBadRequest());
        assertEquals(0, campeonatoRepository.count());
    }
}