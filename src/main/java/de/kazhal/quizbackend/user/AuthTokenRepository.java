package de.kazhal.quizbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    @Query("select t from AuthToken t join fetch t.user where t.tokenHash = :hash")
    Optional<AuthToken> findWithUserByTokenHash(@Param("hash") String hash);
}
