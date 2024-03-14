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
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

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

import org.springframework.stereotype.Component;

@Service("web1TokenFactory")
public class NormalTokenFactory extends AbstractTokenFactory {
    private final TokenRepository tokenRepository;

    public NormalTokenFactory(JdbcTemplate jdbcTemplate, TokenRepository tokenRepository) {
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

        var normalJWT = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Token token = tokenRepository.findByMemberEmail(member.getEmail())
            .orElseGet(() -> Token.builder().build());

        token = Token.builder()
                .email(member.getEmail())
                .tokenValue(normalJWT)
                .createdTime(createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .expiredTime(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .tokenType("ACCESS")
                .member(member) // 이미 생성된 Member 인스턴스
                .build();

        tokenRepository.save(token);
        System.out.println("-----------------토큰 저장 완료-----------------");

        return normalJWT;
    }

    
    @Override
    public Boolean checkToken(String tokenValue, List<String> allowedRoles) {
        try {
            // 토큰 디코드
            Map<String, Object> decodedToken = decodeToken(tokenValue);
            System.out.println("decodedToken = " + decodedToken);

            // 디코드된 토큰에 role과 oauth2Id가 있는지 확인
            if (!decodedToken.containsKey("role") || !decodedToken.containsKey("oauth2Id")) {
                System.out.println("role 또는 oauth2Id가 없습니다.");
                return false;
            }
            System.out.println("role 또는 oauth2Id가 있습니다.");

            String tokenRole = (String) decodedToken.get("role");

            System.out.println("tokenRole = " + tokenRole);
            System.out.println("allowedRoles = " + allowedRoles);


            // 디코드된 토큰의 role이 일치하는지 확인
            if (!allowedRoles.contains(tokenRole)) {
                System.out.println("role이 일치하지 않습니다.");
                return false;
            } 
            System.out.println("role이 일치합니다.");

            System.out.println("토큰이 유효합니다.");

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}