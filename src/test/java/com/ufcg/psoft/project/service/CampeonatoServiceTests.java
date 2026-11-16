package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.campeonato.CampeonatoRequestDTO;
import com.ufcg.psoft.project.dto.campeonato.CampeonatoResponseDTO;
import com.ufcg.psoft.project.dto.integration.*;
import com.ufcg.psoft.project.exception.*;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.service.campeonato.CampeonatoServiceImpl;
import com.ufcg.psoft.project.client.ClienteFootballData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampeonatoServiceTests {

    @InjectMocks
    private CampeonatoServiceImpl campeonatoService;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClienteFootballData clienteFootballData;

    private Usuario admin;
    private String codigoAcessoCorreto = "123456";

    @BeforeEach
    void setUp() {
        admin = Usuario.builder()
                .id(1L)
                .codigoAcesso(codigoAcessoCorreto)
                .perfil(new com.ufcg.psoft.project.model.usuario.perfil.PerfilAdmin())
                .build();
    }

    @Nested
    @DisplayName("Testes de Validação de Administrador (verificarAdmin)")
    class ValidacaoAdminTests {

        @Test
        @DisplayName("Deve lançar UsuarioNaoExisteException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoExisteException.class, () -> {
                campeonatoService.cadastrarCampeonato(new CampeonatoRequestDTO(), 1L, codigoAcessoCorreto);
            });
        }

        @Test
        @DisplayName("Deve lançar PermissaoNegadaException quando o usuário não for admin")
        void deveLancarExcecaoQuandoUsuarioNaoForAdminUnico() {
            Usuario usuarioComum = Usuario.builder().id(2L).codigoAcesso(codigoAcessoCorreto).build();
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioComum));

            assertThrows(PermissaoNegadaException.class, () -> {
                campeonatoService.cadastrarCampeonato(new CampeonatoRequestDTO(), 2L, codigoAcessoCorreto);
            });
        }

        @Test
        @DisplayName("Deve lançar CodigoDeAcessoInvalidoException quando o código estiver incorreto")
        void deveLancarExcecaoQuandoCodigoAcessoIncorreto() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

            assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
                campeonatoService.cadastrarCampeonato(new CampeonatoRequestDTO(), 1L, "ERRADO");
            });
        }
    }

    @Nested
    @DisplayName("Testes dos Fluxos de Sucesso e Regras de Negócio")
    class FluxosPrincipaisTests {

        @BeforeEach
        void setupAdminValido() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        }

        @Test
        @DisplayName("Deve cadastrar campeonato com sucesso quando dados forem válidos")
        void deveCadastrarCampeonatoComSucesso() {
            CampeonatoRequestDTO requestDTO = new CampeonatoRequestDTO();
            Campeonato campeonatoMock = new Campeonato();
            Campeonato campeonatoSalvo = new Campeonato();
            CampeonatoResponseDTO responseDTO = new CampeonatoResponseDTO();
            responseDTO.setNome("Copa do Nordeste");

            when(modelMapper.map(requestDTO, Campeonato.class)).thenReturn(campeonatoMock);
            when(campeonatoRepository.save(campeonatoMock)).thenReturn(campeonatoSalvo);
            when(modelMapper.map(campeonatoSalvo, CampeonatoResponseDTO.class)).thenReturn(responseDTO);

            CampeonatoResponseDTO resultado = campeonatoService.cadastrarCampeonato(requestDTO, 1L, codigoAcessoCorreto);

            assertNotNull(resultado);
            assertEquals("Copa do Nordeste", resultado.getNome());
            verify(campeonatoRepository, times(1)).save(campeonatoMock);
        }

        @Test
        @DisplayName("Deve remover campeonato com sucesso quando ID existir")
        void deveRemoverCampeonatoComSucesso() {
            Long campeonatoId = 10L;
            Campeonato campeonatoEncontrado = new Campeonato();
            when(campeonatoRepository.findById(campeonatoId)).thenReturn(Optional.of(campeonatoEncontrado));

            campeonatoService.removerCampeonato(campeonatoId, 1L, codigoAcessoCorreto);

            verify(campeonatoRepository, times(1)).delete(campeonatoEncontrado);
        }

        @Test
        @DisplayName("Deve lançar CampeonatoNaoExisteException ao tentar remover campeonato inexistente")
        void deveLancarExcecaoAoRemoverInexistente() {
            Long campeonatoId = 10L;
            when(campeonatoRepository.findById(campeonatoId)).thenReturn(Optional.empty());

            assertThrows(CampeonatoNaoExisteException.class, () -> {
                campeonatoService.removerCampeonato(campeonatoId, 1L, codigoAcessoCorreto);
            });
            verify(campeonatoRepository, never()).delete(any(Campeonato.class));
        }

        @Test
        @DisplayName("Deve alterar status do campeonato com sucesso")
        void deveAlterarStatusComSucesso() {
            Long campeonatoId = 10L;
            Campeonato campeonatoEncontrado = new Campeonato();
            campeonatoEncontrado.setAtivo(false);

            Campeonato campeonatoSalvo = new Campeonato();
            CampeonatoResponseDTO responseDTO = new CampeonatoResponseDTO();

            when(campeonatoRepository.findById(campeonatoId)).thenReturn(Optional.of(campeonatoEncontrado));
            when(campeonatoRepository.save(campeonatoEncontrado)).thenReturn(campeonatoSalvo);
            when(modelMapper.map(campeonatoSalvo, CampeonatoResponseDTO.class)).thenReturn(responseDTO);

            campeonatoService.alterarStatus(campeonatoId, true, 1L, codigoAcessoCorreto);

            assertTrue(campeonatoEncontrado.isAtivo());
            verify(campeonatoRepository, times(1)).save(campeonatoEncontrado);
        }
    }

    @Nested
    @DisplayName("Testes de Sincronização com API Externa")
    class SincronizacaoApiTests {

        @Test
        @DisplayName("Deve sincronizar competições criando novos campeonatos se não existirem na API")
        void deveSincronizarECriarNovosCampeonatos() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

            CompetitionDTO compDto = new CompetitionDTO();
            compDto.setId(500L);
            compDto.setName("Champions League");
            compDto.setCode("CL");

            FootballDataResponseDTO apiResponse = new FootballDataResponseDTO();
            apiResponse.setCompetitions(Collections.singletonList(compDto));

            when(clienteFootballData.buscarCampeonatos()).thenReturn(apiResponse);

            when(campeonatoRepository.findByApiId(500L)).thenReturn(Optional.empty());

            campeonatoService.sincronizarCampeonatos(1L, codigoAcessoCorreto);

            verify(campeonatoRepository, times(1)).save(argThat(campeonato ->
                campeonato.getApiId().equals(500L) &&
                    campeonato.getNome().equals("Champions League") &&
                    campeonato.getUrlApi().endsWith("/CL")
            ));
        }

        @Test
        @DisplayName("Deve lançar ProjectException caso a chamada HTTP falhe")
        void deveLancarProjectExceptionQuandoApiFalha() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

            when(clienteFootballData.buscarCampeonatos()).thenThrow(new RuntimeException("Erro de conexão"));

            assertThrows(ProjectException.class, () -> {
                campeonatoService.sincronizarCampeonatos(1L, codigoAcessoCorreto);
            });
        }
    }
}
