package web.web1.Member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.web1.Member.domain.models.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    public Optional<Member> findByOauth2Id(String oauth2Id);
    public Optional<Member> findByName(String username);
    public Optional<Member> findByEmail(String email);
}
