package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.convite.Convite;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.convite.StatusConvite;
import com.ufcg.psoft.project.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConviteRepository extends JpaRepository<Convite, Long> {

    List<Convite> findByConvidadoAndStatus(Usuario convidado, StatusConvite status);

    boolean existsByGrupoAndConvidado(Grupo grupo, Usuario convidado);

    Optional<Convite> findByGrupoAndConvidado(Grupo grupo, Usuario convidado);
}
