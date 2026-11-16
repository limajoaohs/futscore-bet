package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.aposta.Aposta;
import com.ufcg.psoft.project.model.aposta.StatusAposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApostaRepository extends JpaRepository<Aposta, Long> {
    List<Aposta> findByUsuarioIdOrderByCriadaEmDesc(Long usuarioId);
    List<Aposta> findByStatus(StatusAposta status);
}
