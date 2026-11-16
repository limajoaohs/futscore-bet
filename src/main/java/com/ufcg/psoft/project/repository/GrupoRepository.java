package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.grupo.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByNomeContaining(String nome);
    List<Grupo> findByCampeonatoId(Long campeonatoId);
}
