package web.web1.Member.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import web.web1.Config.SecretConfig;

import org.springframework.beans.factory.annotation.Autowired;

import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.repository.MemberRepository;
import web.web1.Member.domain.repository.TokenRepository;

@Service("abstractTokenFactory")
public abstract class AbstractTokenFactory implements TokenFactory {
    protected static final String secretKey = SecretConfig.SECRET;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AbstractTokenFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @Override
    public Map<String, Object> decodeToken(String tokenValue) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Jws<Claims> claimJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenValue);
        return new HashMap<>(claimJws.getBody());
    }

    @Override
    public Boolean isExpired(String exp) {
        long nowMillis = System.currentTimeMillis(); // 현재 시간을 밀리초 단위로 가져옵니다.
        long expMillis = Long.parseLong(exp) * 1000; // exp를 밀리초 단위로 변환합니다.
    
        // 현재 시간이 토큰의 만료 시간보다 이후인 경우 true를 반환합니다.
        return nowMillis > expMillis;
    }

    @Transactional
    public void deleteToken(String oauth2Id) {
        String sql1 = "SELECT email From member WHERE oauth2id = ?";

        String email = jdbcTemplate.queryForObject(sql1, String.class, oauth2Id);
        
        if (email != null) {
            // 두 번째 SQL 문을 실행하여 해당 사용자의 토큰을 삭제합니다.
            String sql2 = "DELETE FROM token WHERE email = ?";
            int rowsAffected = jdbcTemplate.update(sql2, email);
            System.out.println(rowsAffected + " rows deleted.");
        } else {
            System.out.println("No member found with oauth2Id: " + oauth2Id);
        }

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
