package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.usuario.UsuarioPutDTO;
import com.ufcg.psoft.project.dto.usuario.UsuarioPostDTO;
import com.ufcg.psoft.project.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/usuarios",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarUsuario(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.recuperar(id));
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios(
            @RequestParam(required = false, defaultValue = "") String nome) {
        if (nome != null && !nome.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(usuarioService.listarPorNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<?> criarUsuario(
            @RequestBody @Valid UsuarioPostDTO usuarioPostDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.criar(usuarioPostDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid com.ufcg.psoft.project.dto.usuario.LoginDTO loginDTO) {
        return ResponseEntity.ok(usuarioService.login(loginDTO.email(), loginDTO.codigo()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid UsuarioPutDTO usuarioPutDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.alterar(id, codigoAcesso, usuarioPutDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirUsuario(
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {
        usuarioService.remover(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }
}
