package com.ufcg.psoft.project.service;

import com.ufcg.psoft.project.dto.usuario.UsuarioPostDTO;
import com.ufcg.psoft.project.dto.usuario.UsuarioResponseDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.service.usuario.UsuarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTests {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    @DisplayName("Deve criar usuário com sucesso quando o e-mail não estiver cadastrado")
    void deveCriarUsuarioComSucesso() {
        UsuarioPostDTO dto = new UsuarioPostDTO("João Henrique", "joao.lima@ufcg.edu.br", "joaohsl", "Rua Aprígio Veloso, Bodocongó", "123456");
        Usuario usuario = new Usuario();
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();

        when(usuarioRepository.findByEmail("joao.lima@ufcg.edu.br")).thenReturn(Optional.empty());
        when(modelMapper.map(dto, Usuario.class)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(modelMapper.map(usuario, UsuarioResponseDTO.class)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.criar(dto);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário se o e-mail já estiver cadastrado no sistema")
    void deveFalharAoCriarUsuarioComEmailDuplicado() {
        UsuarioPostDTO dto = new UsuarioPostDTO("João Henrique", "joao.lima@ufcg.edu.br", "joaohsl", "Rua Aprígio Veloso, Bodocongó", "123456");
        Usuario usuarioExistente = new Usuario();

        when(usuarioRepository.findByEmail("joao.lima@ufcg.edu.br")).thenReturn(Optional.of(usuarioExistente));

        ProjectException excecao = assertThrows(ProjectException.class, () -> {
            usuarioService.criar(dto);
        });

        assertEquals("O email informado ja esta cadastrado.", excecao.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
