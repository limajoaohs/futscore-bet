package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.model.evento.EventoNotificacao;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.service.notificacao.NotificacaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
public class NotificacaoServiceImplTests {

    private NotificacaoServiceImpl notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoServiceImpl();
    }

    @Test
    @DisplayName("Deve gerar o log da notificação com a formatação exata")
    void deveGerarLogDeNotificacaoCorretamente(CapturedOutput output) {
        Usuario usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        EventoNotificacao evento = EventoNotificacao.ABERTURA_PALPITES;
        String mensagem = "Palpites abertos para Flamengo x Vasco!";

        notificacaoService.notificarUsuario(usuario, evento, mensagem);

        String logEsperado = "[NOTIFICAÇÃO - ABERTURA_PALPITES] Para: João Silva (joao@email.com) -> Palpites abertos para Flamengo x Vasco!";

        assertTrue(output.getOut().contains(logEsperado),
                "O console deveria conter o log formatado exatamente como esperado.");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se o usuário for nulo")
    void naoDeveNotificarSeUsuarioForNulo() {
        EventoNotificacao evento = EventoNotificacao.ABERTURA_PALPITES;
        String mensagem = "Mensagem de teste";

        assertThrows(NullPointerException.class, () -> {
            notificacaoService.notificarUsuario(null, evento, mensagem);
        }, "Deveria lançar NullPointerException ao tentar acessar getNome() de um usuário nulo.");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se o evento for nulo")
    void naoDeveNotificarSeEventoForNulo() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        String mensagem = "Mensagem de teste";

        assertThrows(NullPointerException.class, () -> {
            notificacaoService.notificarUsuario(usuario, null, mensagem);
        }, "Deveria lançar NullPointerException ao tentar acessar name() de um evento nulo.");
    }
}