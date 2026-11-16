package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.convite.ConviteRequestDTO;
import com.ufcg.psoft.project.dto.convite.ConviteResponseDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.convite.Convite;
import com.ufcg.psoft.project.model.convite.StatusConvite;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.grupo.Privacidade;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.ConviteRepository;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.service.convite.ConviteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConviteServiceTests {

    @InjectMocks
    private ConviteServiceImpl conviteService;

    @Mock
    private ConviteRepository conviteRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    private Usuario organizador;
    private Usuario convidado;
    private Grupo grupo;
    private Convite convite;

    @BeforeEach
    public void setUp() {
        organizador = Usuario.builder()
                .id(1L)
                .nome("Organizador")
                .email("org@email.com")
                .codigoAcesso("123456")
                .build();

        convidado = Usuario.builder()
                .id(2L)
                .nome("Convidado")
                .email("conv@email.com")
                .codigoAcesso("654321")
                .build();

        grupo = Grupo.builder()
                .id(1L)
                .nome("Grupo Secreto")
                .privacidade(Privacidade.PRIVADA)
                .organizador(organizador)
                .build();

        convite = Convite.builder()
                .id(1L)
                .grupo(grupo)
                .organizador(organizador)
                .convidado(convidado)
                .status(StatusConvite.PENDENTE)
                .build();
    }

    @Test
    public void testEnviarConviteComSucesso() {
        ConviteRequestDTO requestDTO = ConviteRequestDTO.builder()
                .grupoId(1L)
                .organizadorId(1L)
                .convidadoId(2L)
                .build();

        ConviteResponseDTO responseDTO = ConviteResponseDTO.builder()
                .status(StatusConvite.PENDENTE)
                .build();

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(organizador));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(convidado));
        when(conviteRepository.existsByGrupoAndConvidado(grupo, convidado)).thenReturn(false);
        when(conviteRepository.save(any(Convite.class))).thenReturn(convite);

        when(modelMapper.map(any(Convite.class), eq(ConviteResponseDTO.class))).thenReturn(responseDTO);
        
        ConviteResponseDTO response = conviteService.enviarConvite(requestDTO);

        assertNotNull(response);
        assertEquals(StatusConvite.PENDENTE, response.getStatus());
        verify(conviteRepository, times(1)).save(any(Convite.class));
    }

    @Test
    public void testResponderConviteAceitarComSucesso() {
        ConviteResponseDTO responseDTO = ConviteResponseDTO.builder()
                .status(StatusConvite.ACEITO)
                .build();

        when(conviteRepository.findById(1L)).thenReturn(Optional.of(convite));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(convidado));
        when(conviteRepository.save(any(Convite.class))).thenReturn(convite);

        when(modelMapper.map(any(Convite.class), eq(ConviteResponseDTO.class))).thenReturn(responseDTO);

        ConviteResponseDTO response = conviteService.responderConvite(1L, 2L, "654321", "ACEITAR");

        assertNotNull(response);
        assertEquals(StatusConvite.ACEITO, response.getStatus());
        verify(conviteRepository, times(1)).save(any(Convite.class));
    }

    @Test
    public void testResponderConviteComCodigoInvalido() {
        when(conviteRepository.findById(1L)).thenReturn(Optional.of(convite));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(convidado));

        assertThrows(ProjectException.class, () -> {
            conviteService.responderConvite(1L, 2L, "000000", "ACEITAR");
        });
    }
}