package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.aposta.ApostaRequestDTO;
import com.ufcg.psoft.project.dto.aposta.ResultadoDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.aposta.Palpite1X2;
import com.ufcg.psoft.project.model.aposta.StatusAposta;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.partida.status.Cancelado;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.model.usuario.perfil.PerfilAdmin;
import com.ufcg.psoft.project.repository.*;
import com.ufcg.psoft.project.service.aposta.ApostaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApostaServiceTests {

    @Autowired ApostaService apostaService;
    @Autowired ApostaRepository apostaRepository;
    @Autowired PartidaRepository partidaRepository;
    @Autowired CampeonatoRepository campeonatoRepository;
    @Autowired UsuarioRepository usuarioRepository;

    Usuario apostador;
    Usuario admin;
    Partida partida;

    @BeforeEach
    void setUp() {
        apostador = usuarioRepository.save(Usuario.builder().nome("Ana").email("ana@x.com").username("ana")
                .codigoAcesso("111111").build());
        admin = usuarioRepository.save(Usuario.builder().nome("Adm").email("adm@x.com").username("adm")
                .codigoAcesso("999999").perfil(new PerfilAdmin()).build());
        Campeonato c = campeonatoRepository.save(Campeonato.builder().nome("T").ano("2026").urlApi("x").apiId(77L).build());
        partida = partidaRepository.save(Partida.builder().apiId(500L).campeonato(c)
                .timeMandante("Flamengo").timeVisitante("Palmeiras").dataHora(LocalDateTime.now().plusHours(3)).build());
    }

    @AfterEach
    void tearDown() {
        apostaRepository.deleteAll();
        partidaRepository.deleteAll();
        campeonatoRepository.deleteAll();
        usuarioRepository.deleteAll(java.util.List.of(apostador, admin));
    }

    private BigDecimal saldo() {
        return usuarioRepository.findById(apostador.getId()).orElseThrow().getSaldo();
    }

    @Test
    void apostaVencedoraDebitaEPagaValorVezesOdd() {
        var aposta = apostaService.apostar(apostador.getId(), "111111",
                new ApostaRequestDTO(partida.getId(), Palpite1X2.CASA, new BigDecimal("100.00")));
        assertEquals(0, saldo().compareTo(new BigDecimal("900.00")));
        assertTrue(aposta.odd().compareTo(BigDecimal.ONE) > 0);

        apostaService.registrarResultado(partida.getId(), admin.getId(), "999999", new ResultadoDTO(2, 1));

        assertEquals(StatusAposta.GANHA, apostaRepository.findById(aposta.id()).orElseThrow().getStatus());
        assertEquals(0, saldo().compareTo(new BigDecimal("900.00").add(aposta.retornoPotencial())));
    }

    @Test
    void apostaPerdedoraNaoPaga() {
        var aposta = apostaService.apostar(apostador.getId(), "111111",
                new ApostaRequestDTO(partida.getId(), Palpite1X2.EMPATE, new BigDecimal("50.00")));
        apostaService.registrarResultado(partida.getId(), admin.getId(), "999999", new ResultadoDTO(0, 1));
        assertEquals(StatusAposta.PERDIDA, apostaRepository.findById(aposta.id()).orElseThrow().getStatus());
        assertEquals(0, saldo().compareTo(new BigDecimal("950.00")));
    }

    @Test
    void partidaCanceladaDevolveValor() {
        var aposta = apostaService.apostar(apostador.getId(), "111111",
                new ApostaRequestDTO(partida.getId(), Palpite1X2.FORA, new BigDecimal("30.00")));
        partida.setStatus(new Cancelado());
        partidaRepository.save(partida);
        apostaService.liquidarApostas();
        assertEquals(StatusAposta.ANULADA, apostaRepository.findById(aposta.id()).orElseThrow().getStatus());
        assertEquals(0, saldo().compareTo(new BigDecimal("1000.00")));
    }

    @Test
    void rejeitaSaldoInsuficienteCodigoErradoEMercadoFechado() {
        assertThrows(ProjectException.class, () -> apostaService.apostar(apostador.getId(), "111111",
                new ApostaRequestDTO(partida.getId(), Palpite1X2.CASA, new BigDecimal("1000.01"))));
        assertThrows(ProjectException.class, () -> apostaService.apostar(apostador.getId(), "000000",
                new ApostaRequestDTO(partida.getId(), Palpite1X2.CASA, BigDecimal.TEN)));
        partida.setDataHora(LocalDateTime.now().minusMinutes(1));
        partidaRepository.save(partida);
        assertThrows(ProjectException.class, () -> apostaService.apostar(apostador.getId(), "111111",
                new ApostaRequestDTO(partida.getId(), Palpite1X2.CASA, BigDecimal.TEN)));
        assertEquals(0, saldo().compareTo(new BigDecimal("1000.00")));
    }

    @Test
    void naoAdminNaoRegistraResultado() {
        assertThrows(ProjectException.class, () ->
                apostaService.registrarResultado(partida.getId(), apostador.getId(), "111111", new ResultadoDTO(1, 0)));
    }

    @Test
    void oddsTemMargemDaCasa() {
        double soma = 0;
        for (Palpite1X2 p : Palpite1X2.values()) soma += 1 / partida.odd(p).doubleValue();
        assertTrue(soma > 1.0 && soma < 1.12, "overround=" + soma);
    }
}
