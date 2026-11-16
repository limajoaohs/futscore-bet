package com.ufcg.psoft.project.service.usuario;

import com.ufcg.psoft.project.dto.usuario.UsuarioPutDTO;
import com.ufcg.psoft.project.dto.usuario.UsuarioPostDTO;
import com.ufcg.psoft.project.dto.usuario.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO alterar(Long id, String codigoAcesso, UsuarioPutDTO usuarioPutDTO);

    List<UsuarioResponseDTO> listar();

    UsuarioResponseDTO recuperar(Long id);

    UsuarioResponseDTO criar(UsuarioPostDTO usuarioPostDTO);

    void remover(Long id, String codigoAcesso);

    List<UsuarioResponseDTO> listarPorNome(String nome);

    UsuarioResponseDTO login(String email, String codigoAcesso);
}
