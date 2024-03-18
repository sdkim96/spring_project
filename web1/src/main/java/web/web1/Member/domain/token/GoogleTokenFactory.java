package web.web1.Member.domain.token;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.Claims;
import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.models.Token;
import web.web1.Member.domain.repository.TokenRepository;
import web.web1.Member.domain.token.TokenFactory;
import web.web1.Config.SecretConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service("googleTokenFactory")
@Primary
public class GoogleTokenFactory extends AbstractTokenFactory {
    private final TokenRepository tokenRepository;

    public GoogleTokenFactory(JdbcTemplate jdbcTemplate, TokenRepository tokenRepository) {
        super(jdbcTemplate);
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String createToken(Member member, String code) {
        long expirationTimeLong = 3600 * 1000; //1시간
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);

        String role = member.getRole();
        String oauth2Id = member.getOauth2Id();
        

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("oauth2Id", oauth2Id);
        claims.put("code", code);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        System.out.println("key = " + key);

        var googleJWT = Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(createdDate)
                        .setExpiration(expirationDate)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        Token token = tokenRepository.findByMemberEmail(member.getEmail())
        .orElseGet(() -> Token.builder().build());

        // 빌더를 사용한 토큰 정보 설정
        token = Token.builder()
            .email(member.getEmail())
            .tokenValue(googleJWT)
            .createdTime(createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            .expiredTime(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            .tokenType("ACCESS")
            .member(member) // 이미 생성된 Member 인스턴스
            .build();

        tokenRepository.save(token); // 토큰 저장
        System.out.println("-----------------토큰 저장 완료-----------------");

        return googleJWT;
    }

    
}
