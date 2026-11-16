package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.palpite.Palpite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PalpiteRepository extends JpaRepository<Palpite, Long> {
    boolean existsByUsuarioIdAndPartidaIdAndGrupoId(Long usuarioId, Long partidaId, Long grupoId);
    List<Palpite> findByPartidaId(Long partidaId);
    List<Palpite> findByGrupoId(Long grupoId);
}
