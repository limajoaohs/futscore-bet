package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.campeonato.Campeonato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long>{

    Optional<Campeonato> findByApiId(Long apiId);
}
