package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.palpite.PalpiteRequestDTO;
import com.ufcg.psoft.project.dto.palpite.PalpiteResponseDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.palpite.Palpite;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.PalpiteRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.service.palpite.PalpiteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.ufcg.psoft.project.model.partida.status.Cancelado;
import com.ufcg.psoft.project.model.partida.status.EmAndamento;
import com.ufcg.psoft.project.model.partida.status.Finalizado;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PalpiteServiceTests {

    @Mock
    private PalpiteRepository palpiteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PalpiteServiceImpl palpiteService;

    private Usuario usuario;
    private Campeonato campeonato;
    private Partida partida;
    private Grupo grupo;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("usuario_teste");

        campeonato = new Campeonato();
        campeonato.setId(10L);
        campeonato.setNome("Brasileirão");
        campeonato.setAtivo(true);

        partida = new Partida();
        partida.setId(100L);
        partida.setCampeonato(campeonato);
        partida.setTimeMandante("Flamengo");
        partida.setTimeVisitante("Vasco");
        partida.setDataHora(LocalDateTime.now().plusHours(2));

        grupo = new Grupo();
        grupo.setId(50L);
        grupo.setNome("Grupo da Família");
        grupo.setCampeonato(campeonato);
        grupo.setParticipantes(List.of(usuario));
        grupo.setMinutosParaAbertura(180);
        grupo.setMinutosParaFechamento(30);
    }

    @Test
    @DisplayName("Deve registrar um palpite com sucesso")
    void deveRegistrarPalpiteComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        when(palpiteRepository.existsByUsuarioIdAndPartidaIdAndGrupoId(1L, 100L, 50L)).thenReturn(false);

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        when(palpiteRepository.save(any(Palpite.class))).thenAnswer(invocation -> {
            Palpite palpiteSalvo = invocation.getArgument(0);
            palpiteSalvo.setId(999L);
            return palpiteSalvo;
        });

        PalpiteResponseDTO mockResponse = PalpiteResponseDTO.builder()
                .id(999L)
                .golsMandante(2)
                .golsVisitante(1)
                .partidaTimeMandante("Flamengo")
                .partidaTimeVisitante("Vasco")
                .build();
        when(modelMapper.map(any(Palpite.class), eq(PalpiteResponseDTO.class))).thenReturn(mockResponse);

        PalpiteResponseDTO resultado = palpiteService.registrarPalpite(novoPalpiteDTO);

        assertNotNull(resultado);
        assertEquals(999L, resultado.getId());
        assertEquals(2, resultado.getGolsMandante());
        assertEquals(1, resultado.getGolsVisitante());
        assertEquals("Flamengo", resultado.getPartidaTimeMandante());
        assertEquals("Vasco", resultado.getPartidaTimeVisitante());

        verify(palpiteRepository, times(1)).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se o usuário não for encontrado")
    void naoDeveRegistrarPalpiteUsuarioNaoEncontrado() {
        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(2L, 100L, 50L, 2, 1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se a partida não for encontrado")
    void naoDeveRegistrarPalpitePartidaNaoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.empty());

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Partida não encontrada", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se o grupo não for encontrado")
    void naoDeveRegistrarPalpiteGrupoNaoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.empty());

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Grupo não encontrado", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se a partida não pertence ao campeonato do grupo")
    void naoDeveRegistrarPalpiteSePartidaNaoPertenceAoCampeonatoDoGrupo() {
        Campeonato campeonatoErrado = new Campeonato();
        campeonatoErrado.setId(99L);
        campeonatoErrado.setNome("Champions League");

        partida.setCampeonato(campeonatoErrado);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não pertence ao campeonato deste grupo.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se o usuário não faz parte do grupo")
    void naoDeveRegistrarPalpiteUsuarioNaoPertenceAoGrupo() {
        Usuario usuarioErrado = new Usuario();
        usuarioErrado.setId(2L);
        usuarioErrado.setNome("usuario_teste");

        grupo.setParticipantes(List.of(usuarioErrado));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("O usuário não é participante deste grupo.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se o usuário já registrou um palpite nessa partida nesse grupo")
    void naoDeveRegistrarPalpiteSeUsuarioJaRegistrouPalpiteNessaPartidaNesseGrupo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        when(palpiteRepository.existsByUsuarioIdAndPartidaIdAndGrupoId(1L, 100L, 50L)).thenReturn(true);

        PalpiteRequestDTO palpiteRepetidoDTO = new PalpiteRequestDTO(1L, 100L, 50L, 3, 2);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                palpiteService.registrarPalpite(palpiteRepetidoDTO));

        assertEquals("Você já registrou um palpite para esta partida neste grupo.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Deve editar palpite com sucesso quando for o dono e estiver no prazo")
    void deveEditarPalpiteComSucesso() {
        Palpite palpite = new Palpite();
        palpite.setId(10L);
        palpite.setUsuario(usuario);
        palpite.setPartida(partida);
        palpite.setGrupo(grupo);
        palpite.setGolsMandante(0);
        palpite.setGolsVisitante(0);

        when(palpiteRepository.findById(10L)).thenReturn(Optional.of(palpite));
        when(palpiteRepository.save(any(Palpite.class))).thenReturn(palpite);

        PalpiteResponseDTO mockResponse = PalpiteResponseDTO.builder()
                .id(10L)
                .golsMandante(2)
                .golsVisitante(1)
                .partidaTimeMandante("Flamengo")
                .partidaTimeVisitante("Vasco")
                .build();
        when(modelMapper.map(any(Palpite.class), eq(PalpiteResponseDTO.class))).thenReturn(mockResponse);

        PalpiteRequestDTO dtoEdicao = new PalpiteRequestDTO(usuario.getId(), partida.getId(), grupo.getId(), 2, 1);
        PalpiteResponseDTO response = palpiteService.editarPalpite(10L, dtoEdicao);

        assertNotNull(response);
        assertEquals(2, response.getGolsMandante());
        assertEquals(1, response.getGolsVisitante());
        verify(palpiteRepository).save(palpite);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar editar palpite de outro usuário")
    void deveLancarErroQuandoNaoForDono() {
        Usuario donoReal = new Usuario();
        donoReal.setId(99L);

        Palpite palpite = new Palpite();
        palpite.setId(10L);
        palpite.setUsuario(donoReal);

        when(palpiteRepository.findById(10L)).thenReturn(Optional.of(palpite));

        PalpiteRequestDTO dtoEdicao = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.editarPalpite(10L, dtoEdicao));

        assertEquals("Você não tem permissão para editar este palpite", exception.getMessage());
        verify(palpiteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar editar palpite após o horário de fechamento")
    void deveLancarErroQuandoForaDoPrazo() {
        partida.setDataHora(LocalDateTime.now().plusMinutes(20));

        Palpite palpite = new Palpite();
        palpite.setId(10L);
        palpite.setUsuario(usuario);
        palpite.setPartida(partida);
        palpite.setGrupo(grupo);

        when(palpiteRepository.findById(10L)).thenReturn(Optional.of(palpite));

        PalpiteRequestDTO dtoEdicao = new PalpiteRequestDTO(usuario.getId(), partida.getId(), grupo.getId(), 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.editarPalpite(10L, dtoEdicao)
        );

        assertEquals("O período de edição de palpites para esta partida já acabou.", exception.getMessage());
        verify(palpiteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve registrar o palpite se ainda não estiver no período de abertura")
    void naoDeveRegistrarPalpiteAntesDaAbertura() {
        partida.setDataHora(LocalDateTime.now().plusHours(4));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se a partida estiver com status Finalizado")
    void naoDeveRegistrarPalpiteQuandoPartidaFinalizada() {
        partida.setStatus(new Finalizado());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se a partida estiver com status Cancelado")
    void naoDeveRegistrarPalpiteQuandoPartidaCancelada() {
        partida.setStatus(new Cancelado());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se a partida estiver com status EmAndamento")
    void naoDeveRegistrarPalpiteQuandoPartidaEmAndamento() {
        partida.setStatus(new EmAndamento());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite se o tempo já foi encerrado")
    void naoDeveRegistrarPalpiteAposFechamento() {
        partida.setDataHora(LocalDateTime.now().plusMinutes(20));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite instantes antes da abertura")
    void naoDeveRegistrarPalpiteInstantesAntesDaAbertura() {
        partida.setDataHora(LocalDateTime.now().plusMinutes(grupo.getMinutosParaAbertura()).plusSeconds(5));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Deve registrar o palpite instantes após a abertura")
    void deveRegistrarPalpiteInstantesAposAAbertura() {
        partida.setDataHora(LocalDateTime.now().plusMinutes(grupo.getMinutosParaAbertura()).minusSeconds(5));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));
        when(palpiteRepository.existsByUsuarioIdAndPartidaIdAndGrupoId(1L, 100L, 50L)).thenReturn(false);

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        when(palpiteRepository.save(any(Palpite.class))).thenAnswer(invocation -> {
            Palpite palpiteSalvo = invocation.getArgument(0);
            palpiteSalvo.setId(999L);
            return palpiteSalvo;
        });

        PalpiteResponseDTO mockResponse = PalpiteResponseDTO.builder().id(999L).build();
        when(modelMapper.map(any(Palpite.class), eq(PalpiteResponseDTO.class))).thenReturn(mockResponse);

        PalpiteResponseDTO resultado = palpiteService.registrarPalpite(novoPalpiteDTO);

        assertNotNull(resultado);
        verify(palpiteRepository, times(1)).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Deve registrar o palpite no limite do fechamento (instantes antes de fechar)")
    void deveRegistrarPalpiteNoLimiteDoFechamento() {
        partida.setDataHora(LocalDateTime.now().plusMinutes(grupo.getMinutosParaFechamento()).plusSeconds(5));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));
        when(palpiteRepository.existsByUsuarioIdAndPartidaIdAndGrupoId(1L, 100L, 50L)).thenReturn(false);

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        when(palpiteRepository.save(any(Palpite.class))).thenAnswer(invocation -> {
            Palpite palpiteSalvo = invocation.getArgument(0);
            palpiteSalvo.setId(999L);
            return palpiteSalvo;
        });

        PalpiteResponseDTO mockResponse = PalpiteResponseDTO.builder().id(999L).build();
        when(modelMapper.map(any(Palpite.class), eq(PalpiteResponseDTO.class))).thenReturn(mockResponse);

        PalpiteResponseDTO resultado = palpiteService.registrarPalpite(novoPalpiteDTO);

        assertNotNull(resultado);
        verify(palpiteRepository, times(1)).save(any(Palpite.class));
    }

    @Test
    @DisplayName("Não deve registrar o palpite instantes após o fechamento")
    void naoDeveRegistrarPalpiteInstantesAposOFechamento() {
        partida.setDataHora(LocalDateTime.now().plusMinutes(grupo.getMinutosParaFechamento()).minusSeconds(5));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(partidaRepository.findById(100L)).thenReturn(Optional.of(partida));
        when(grupoRepository.findById(50L)).thenReturn(Optional.of(grupo));

        PalpiteRequestDTO novoPalpiteDTO = new PalpiteRequestDTO(1L, 100L, 50L, 2, 1);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                palpiteService.registrarPalpite(novoPalpiteDTO));

        assertEquals("Esta partida não está aberta para palpites.", exception.getMessage());
        verify(palpiteRepository, never()).save(any(Palpite.class));
    }
}