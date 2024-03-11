package web.web1.Oauth.token;

public interface OAuth2Token {
    
String getCode();
String getClientId();
String getClientSecret();
String getRedirectUri();
String getGrantType();


}
