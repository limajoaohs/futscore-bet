package com.ufcg.psoft.project.repository;
import com.ufcg.psoft.project.model.partida.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByCampeonatoId(Long campeonatoId);
    java.util.Optional<Partida> findByApiId(Long apiId);
    List<Partida> findByDataHoraAfter(LocalDateTime dataHora);
    List<Partida> findByDataHoraGreaterThanEqual(LocalDateTime dataHora);
}
