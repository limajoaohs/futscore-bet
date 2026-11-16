package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByNomeContaining(String nome);

    Optional<Usuario> findByEmail(String email);

    // trava a linha para evitar gasto duplo do saldo em apostas simultaneas
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
