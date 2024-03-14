package web.web1.Member.domain.token;

import java.util.List;
import java.util.Map;

import web.web1.Member.domain.models.Member;

public interface TokenFactory {
    String createToken(Member member, String code);
    Map<String, Object> decodeToken(String token);
    
    Boolean checkToken(String tokenValue, List<String> allowedRoles);
    Boolean isExpired(String exp);
    void deleteToken(String oauth2Id);
}
