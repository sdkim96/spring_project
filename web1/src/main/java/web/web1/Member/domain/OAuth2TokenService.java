package web.web1.Member.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.api.client.util.Value;

import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.repository.TokenRepository;
import web.web1.Member.domain.token.GoogleTokenFactory;

@Service
public class OAuth2TokenService {

    private final TokenRepository tokenRepository;
    private final GoogleTokenFactory googleTokenFactory;

    @Autowired
    public OAuth2TokenService(JdbcTemplate jdbcTemplate, TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        this.googleTokenFactory = new GoogleTokenFactory(jdbcTemplate, tokenRepository); // 수정됨
    }

    //토큰 발급을 위한 메소드
    public String makeJwtToken (Member member, String serviceType, String code) {

        // 매개변수 확인
        System.out.println("------makeJwtToken 시작------");
        System.out.println("serviceType: " + serviceType);
        System.out.println("Code: " + code);
        String token;

        // 만약 구글이면 구글 토큰을 만들어서 반환
        if(serviceType.equals("google")) {
            token = googleTokenFactory.createToken(member, code); 
        } else {
            throw new IllegalArgumentException("지원하지 않는 서비스입니다.");
        }

        System.out.println(serviceType + " Token: " + token);
        System.out.println("------makeJwtToken 종료------");
        // 토큰 반환
        return token;

    }
}