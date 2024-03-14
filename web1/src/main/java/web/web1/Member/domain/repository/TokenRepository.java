package web.web1.Member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.web1.Member.domain.models.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    public Optional<Token> findByMemberEmail(String email);

}
