package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.dto.palpite.PalpiteRequestDTO;
import com.ufcg.psoft.project.dto.palpite.PalpiteResponseDTO;
import com.ufcg.psoft.project.service.palpite.PalpiteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/palpites",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PalpiteController {

    @Autowired
    PalpiteService palpiteService;

    @PostMapping
    public ResponseEntity<?> registrarPalpite(
            @RequestBody @Valid PalpiteRequestDTO palpiteRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(palpiteService.registrarPalpite(palpiteRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PalpiteResponseDTO> editarPalpite(
            @PathVariable Long id,
            @RequestBody @Valid PalpiteRequestDTO palpiteRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(palpiteService.editarPalpite(id, palpiteRequestDTO));
    }
}
