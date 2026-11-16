package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.grupo.GrupoRequestDTO;
import com.ufcg.psoft.project.dto.grupo.GrupoResponseDTO;
import com.ufcg.psoft.project.dto.grupo.CriteriosDesempateDTO;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.grupo.Privacidade;
import com.ufcg.psoft.project.model.grupo.CriterioDesempate;
import com.ufcg.psoft.project.model.grupo.CriterioDesempateFactory;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.service.grupo.GrupoServiceImpl;
import com.ufcg.psoft.project.exception.ProjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GrupoServiceTests {

    @InjectMocks
    private GrupoServiceImpl grupoServiceImpl;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Campeonato campeonatoValido;
    private Usuario organizador;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CriterioDesempateFactory criterioDesempateFactory;

    @BeforeEach
    void setUp() {
        campeonatoValido = new Campeonato();
        campeonatoValido.setId(1L);
        campeonatoValido.setNome("Brasileirão");
        campeonatoValido.setAtivo(true);

        organizador = new Usuario();
        organizador.setId(10L);
        organizador.setNome("Criador Teste");
    }

    @Test
    @DisplayName("Deve criar grupo público com sucesso")
    void deveCriarGrupoPublicoComSucesso() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoValido));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));

        GrupoRequestDTO novoGrupoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PUBLICA, 5, 1L, 10L, 120, 30);

        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> {
            Grupo grupoSalvo = invocation.getArgument(0);
            grupoSalvo.setId(100L);
            return grupoSalvo;
        });

        GrupoResponseDTO mockResponse = GrupoResponseDTO.builder()
                .id(100L)
                .nome("Bolão da Galera")
                .organizadorNome("Criador Teste")
                .campeonatoNome("Brasileirão")
                .privacidade("PUBLICA")
                .minutosParaAbertura(120)
                .minutosParaFechamento(30)
                .build();

        when(modelMapper.map(any(Grupo.class), eq(GrupoResponseDTO.class))).thenReturn(mockResponse);

        GrupoResponseDTO resultado = grupoServiceImpl.criarGrupo(novoGrupoDTO);

        assertNotNull(resultado.getId());
        assertEquals("Bolão da Galera", resultado.getNome());
        assertEquals("Criador Teste", resultado.getOrganizadorNome());
        assertEquals("Brasileirão", resultado.getCampeonatoNome());
        assertEquals("PUBLICA", resultado.getPrivacidade());
        assertEquals(120, resultado.getMinutosParaAbertura());
        assertEquals(30, resultado.getMinutosParaFechamento());

        verify(grupoRepository, times(1)).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Deve criar grupo privado com sucesso")
    void deveCriarGrupoPrivadoComSucesso() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoValido));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));

        GrupoRequestDTO novoGrupoDTO = new GrupoRequestDTO("Bolão da Galera", "Grupo para os amigos", Privacidade.PRIVADA, 5, 1L, 10L, 120, 30);

        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> {
            Grupo grupoSalvo = invocation.getArgument(0);
            grupoSalvo.setId(100L);
            return grupoSalvo;
        });

        GrupoResponseDTO mockResponse = GrupoResponseDTO.builder()
                .id(100L)
                .nome("Bolão da Galera")
                .organizadorNome("Criador Teste")
                .campeonatoNome("Brasileirão")
                .privacidade("PRIVADA")
                .minutosParaAbertura(120)
                .minutosParaFechamento(30)
                .build();

        when(modelMapper.map(any(Grupo.class), eq(GrupoResponseDTO.class))).thenReturn(mockResponse);

        GrupoResponseDTO resultado = grupoServiceImpl.criarGrupo(novoGrupoDTO);

        assertNotNull(resultado.getId());
        assertEquals("Bolão da Galera", resultado.getNome());
        assertEquals("Criador Teste", resultado.getOrganizadorNome());
        assertEquals("Brasileirão", resultado.getCampeonatoNome());
        assertEquals("PRIVADA", resultado.getPrivacidade());
        assertEquals(120, resultado.getMinutosParaAbertura());
        assertEquals(30, resultado.getMinutosParaFechamento());

        verify(grupoRepository, times(1)).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Não deve criar grupo se o campeonato não for encontrado")
    void naoDeveCriarGrupoCampeonatoNaoEncontrado() {
        GrupoRequestDTO novoGrupoDTO = new GrupoRequestDTO("Bolão Inválido", "Desc", Privacidade.PUBLICA, 5, 2L, 10L, 120, 30);

        assertThrows(RuntimeException.class, () -> grupoServiceImpl.criarGrupo(novoGrupoDTO));

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Não deve criar grupo se o campeonato associado estiver inativo")
    void naoDeveCriarGrupoCampeonatoInativo() {
        Campeonato campeonatoInativo = new Campeonato();
        campeonatoInativo.setId(2L);
        campeonatoInativo.setAtivo(false);
        when(campeonatoRepository.findById(2L)).thenReturn(Optional.of(campeonatoInativo));

        GrupoRequestDTO novoGrupoDTO = new GrupoRequestDTO("Bolão Inválido", "Desc", Privacidade.PUBLICA, 5, 2L, 10L, 120, 30);

        assertThrows(RuntimeException.class, () -> grupoServiceImpl.criarGrupo(novoGrupoDTO));

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Não deve criar grupo se o usuário não for encontrado")
    void naoDeveCriarGrupoUsuarioNaoEncontrado() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoValido));

        GrupoRequestDTO novoGrupoDTO = new GrupoRequestDTO("Bolão Inválido", "Desc", Privacidade.PUBLICA, 5, 1L, 11L, 120, 30);

        assertThrows(RuntimeException.class, () -> grupoServiceImpl.criarGrupo(novoGrupoDTO));

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Deve configurar criterios de desempate com sucesso")
    void deveConfigurarCriteriosDeDesempateComSucesso() {
        Grupo grupo = new Grupo();
        grupo.setId(100L);
        grupo.setOrganizador(organizador);

        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));

        CriteriosDesempateDTO dto = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GrupoResponseDTO mockResponse = GrupoResponseDTO.builder()
                .id(100L)
                .criteriosDesempate(dto.getCriterios())
                .build();

        when(modelMapper.map(any(Grupo.class), eq(GrupoResponseDTO.class))).thenReturn(mockResponse);

        GrupoResponseDTO resultado = grupoServiceImpl.configurarCriteriosDesempate(100L, 10L, dto);

        assertNotNull(resultado);
        assertEquals(dto.getCriterios(), resultado.getCriteriosDesempate());
        verify(grupoRepository, times(1)).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Nao deve configurar criterios de desempate se o usuario nao for o organizador")
    void naoDeveConfigurarCriteriosSeUsuarioNaoForOrganizador() {
        Grupo grupo = new Grupo();
        grupo.setId(100L);
        grupo.setOrganizador(organizador);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(11L);

        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(11L)).thenReturn(Optional.of(outroUsuario));

        CriteriosDesempateDTO dto = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        assertThrows(com.ufcg.psoft.project.exception.AcessoNegadoException.class, () ->
                grupoServiceImpl.configurarCriteriosDesempate(100L, 11L, dto)
        );

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Nao deve configurar criterios de desempate se o grupo nao for encontrado")
    void naoDeveConfigurarCriteriosSeGrupoNaoForEncontrado() {
        when(grupoRepository.findById(100L)).thenReturn(Optional.empty());

        CriteriosDesempateDTO dto = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        assertThrows(RuntimeException.class, () ->
                grupoServiceImpl.configurarCriteriosDesempate(100L, 10L, dto)
        );

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Nao deve configurar criterios de desempate se houver duplicidade")
    void naoDeveConfigurarCriteriosSeHouverDuplicidade() {
        Grupo grupo = new Grupo();
        grupo.setId(100L);
        grupo.setOrganizador(organizador);

        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));

        CriteriosDesempateDTO dto = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MENOR_ERRO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        assertThrows(RuntimeException.class, () ->
                grupoServiceImpl.configurarCriteriosDesempate(100L, 10L, dto)
        );

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Nao deve configurar criterios de desempate se a quantidade for invalida")
    void naoDeveConfigurarCriteriosSeQuantidadeInvalida() {
        Grupo grupo = new Grupo();
        grupo.setId(100L);
        grupo.setOrganizador(organizador);

        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));

        CriteriosDesempateDTO dtoMenos = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE"
        ));

        CriteriosDesempateDTO dtoMais = new CriteriosDesempateDTO(java.util.List.of(
                "MENOR_ERRO", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR", "OUTRO_CRITERIO"
        ));

        assertThrows(RuntimeException.class, () ->
                grupoServiceImpl.configurarCriteriosDesempate(100L, 10L, dtoMenos)
        );

        assertThrows(RuntimeException.class, () ->
                grupoServiceImpl.configurarCriteriosDesempate(100L, 10L, dtoMais)
        );

        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    @DisplayName("Nao deve configurar criterios de desempate se houver criterio inexistente")
    void naoDeveConfigurarCriteriosSeCriterioInexistente() {
        Grupo grupo = new Grupo();
        grupo.setId(100L);
        grupo.setOrganizador(organizador);

        when(grupoRepository.findById(100L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(organizador));

        CriteriosDesempateDTO dto = new CriteriosDesempateDTO(java.util.List.of(
                "MAIOR_NUMERO_DE_GOLS", "MAIOR_PLACAR_EXATO", "MAIOR_ACERTO_EMPATE", "MAIOR_ACERTO_VENCEDOR"
        ));

        assertThrows(RuntimeException.class, () ->
                grupoServiceImpl.configurarCriteriosDesempate(100L, 10L, dto)
        );

        verify(grupoRepository, never()).save(any(Grupo.class));
    }
}