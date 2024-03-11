package web.web1.Oauth.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServlet;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import jakarta.servlet.http.HttpServletRequest; // 필요한 임포트 추가


import lombok.val;

public class GoogleToken implements OAuth2Token{
    
    private String clientId = "162422478014-k7801p5p52th4qqn4iur14452kg6aolh.apps.googleusercontent.com";
    private String clientSecret = "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";
    private String redirectUri = "http://localhost:8080/login/oauth2/code/google";
    private String grantType = "authorization_code";
    private String code = "";

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

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
