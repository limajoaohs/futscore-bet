package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.RegraPontuacaoRequestDTO;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.RegraPontuacao;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.service.regrapontuacao.RegraPontuacaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ufcg.psoft.project.exception.AcessoNegadoException;

@ExtendWith(MockitoExtension.class)
public class RegraPontuacaoServiceTests {

    @InjectMocks
    private RegraPontuacaoServiceImpl regraPontuacaoService;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Grupo grupo;
    private Usuario organizador;
    private Usuario outroUsuario;
    private RegraPontuacaoRequestDTO dto;

    @BeforeEach
    void setUp() {
        organizador = new Usuario();
        organizador.setId(10L);

        outroUsuario = new Usuario();
        outroUsuario.setId(20L);

        grupo = new Grupo();
        grupo.setId(100L);
        grupo.setOrganizador(organizador);

        dto = new RegraPontuacaoRequestDTO(2, 1, 5, 10, 15);
    }

    @Test
    @DisplayName("Deve configurar regras de pontuação com sucesso quando o usuário for o organizador")
    void deveConfigurarRegrasComSucesso() {
        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));
        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupo);

        RegraPontuacao resultado = regraPontuacaoService.configurarRegraPontuacao(100L, 10L, dto);

        assertNotNull(resultado);
        assertEquals(2, resultado.getPontosAcertoVencedor());
        assertEquals(1, resultado.getPontosAcertoEmpate());
        assertEquals(5, resultado.getPontosPlacarExato());
        assertEquals(10, resultado.getPontosBonusRodada());
        assertEquals(15, resultado.getPontosBonusMataMata());
        verify(grupoRepository, times(1)).save(grupo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao configurar regras quando o usuário não for o organizador")
    void deveLancarExcecaoQuandoNaoForOrganizador() {
        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(outroUsuario));

        AcessoNegadoException exception = assertThrows(AcessoNegadoException.class, () ->
                regraPontuacaoService.configurarRegraPontuacao(100L, 20L, dto));

        assertEquals("Apenas o organizador do grupo pode alterar as regras de pontuação!", exception.getMessage());
        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao configurar regras quando o grupo não for encontrado")
    void deveLancarExcecaoQuandoGrupoNaoEncontrado() {
        when(grupoRepository.findById(100L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                regraPontuacaoService.configurarRegraPontuacao(100L, 10L, dto));

        assertEquals("Grupo não encontrado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao configurar regras quando o usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                regraPontuacaoService.configurarRegraPontuacao(100L, 10L, dto));

        assertEquals("Usuário não encontrado!", exception.getMessage());
    }
}
