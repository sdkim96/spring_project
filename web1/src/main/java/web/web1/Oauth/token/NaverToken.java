package web.web1.Oauth.token;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Value;

public class NaverToken implements OAuth2Token {

    private String clientId = "8e3e3e3e3e3e3e3e3e3e";
    private String clientSecret = "";
    private String redirectUri = "http://localhost:8080/oauth/naver";
    private String grantType = "authorization_code";
    private String code = "code";

    public String getClientId() {
        return clientId;
    }  

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }
    
}
