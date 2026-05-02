package com.epti.backend.repository;

import com.epti.backend.model.User;
import com.epti.backend.model.enums.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findActiveUsers();

    List<User> findByTurma(Turma turma);

    List<User> findByHasPaid(boolean hasPaid);

    @Query("SELECT u FROM User u WHERE u.hasPaid = false AND u.turma IN :turmas")
    List<User> findByHasPaidFalseAndTurmaIn(@Param("turmas") Turma... turmas);

    @Query("SELECT COUNT(u) FROM User u WHERE u.hasPaid = false AND u.turma IN :turmas")
    long countUnpaidUsersByTurmas(@Param("turmas") Turma... turmas);

    @Query("SELECT u FROM User u WHERE u.hasPaid = false ORDER BY u.fullName")
    List<User> findUnpaidUsersOrderByName();

    @Query("SELECT u FROM User u WHERE u.turma = :turma AND u.hasPaid = false ORDER BY u.fullName")
    List<User> findUnpaidUsersByTurma(@Param("turma") Turma turma);

    boolean existsByEmailAndTurma(String email, Turma turma);
}
