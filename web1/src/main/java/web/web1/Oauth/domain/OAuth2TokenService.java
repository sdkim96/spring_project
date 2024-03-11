package web.web1.Oauth.domain;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.api.client.util.Value;

import web.web1.Oauth.token.*;
// import java.math.BigInteger;
// import java.security.SecureRandom;

@Service
// @PropertySource("classpath:application.properties")
public class OAuth2TokenService {

    private OAuth2Token tokenProvider;

    // @Value("${spring.security.oauth2.client.registration.google.clientId}")
    // private String clientID;

    // @Value("${spring.security.oauth2.client.registration.google.clientSecret}")
    // private String clientSecret;

    public String oauth2MakeServiceProviderGetReqest(String serviceType) {

        System.out.println("serviceType: " + serviceType);

        if(serviceType.equals("naver")) {
            this.tokenProvider = new NaverToken();
        } else if(serviceType.equals("google")) {
            this.tokenProvider = new GoogleToken();
        } else {
            throw new IllegalArgumentException("지원하지 않는 서비스입니다.");
        }
    

        String clientID = this.tokenProvider.getClientId();
        String clientSecret = this.tokenProvider.getClientSecret();
        String code = this.tokenProvider.getCode();
        String redirectUri = this.tokenProvider.getRedirectUri();
        String grantType = this.tokenProvider.getGrantType();
        

        String url = "";

        if(this.tokenProvider instanceof NaverToken) {
            url = "https://nid.naver.com/oauth2.0/authorize?client_id=" 
            + clientID + "&client_secret=" + clientSecret + "&redirect_uri=" 
            + redirectUri + "&grant_type=" + grantType + "&code=" + code;
        } else if(this.tokenProvider instanceof GoogleToken) {
            url = "https://oauth2.googleapis.com/token?code="
            + code + "&client_id=" + clientID + "&client_secret=" 
            + clientSecret + "&grant_type=" + "authorization_code" + "&redirect_uri=" + redirectUri;
        }
        System.out.println(url);
        return url;

    }

    @Autowired
    private RestTemplate restTemplate;

    public String exchangeCodeForAccessToken(String decode, String clientId, String clientSecret, String redirectUri) {
        // Token 엔드포인트 URL

        System.out.println("---exchangeCodeForAccessToken 시작---");
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        // 요청 본문을 구성하기 위한 UriComponentsBuilder 사용
        String requestBody = UriComponentsBuilder.newInstance()
                .queryParam("code", decode)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("grant_type", "authorization_code")
                .toUriString().substring(1); // 맨 앞의 '?' 제거


        System.out.println("requestBody: " + requestBody);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HttpEntity 객체 생성
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        System.out.println(requestEntity);

        // POST 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenEndpoint, requestEntity, String.class);

        // 응답 본문 반환
        System.out.println(responseEntity.getBody());
        return responseEntity.getBody();
    }
    // 1. 네이버냐 구글이냐에 따라 네이버면 ../token/NaverToken의 객체 불러오기 구글이면 GoogleToken 객체 불러오기
    // 2. 해당 서비스에 대해 필요한 조건을 찾기. naver면 
        // 2-1. client_id(),
        // 2-2.response_type(이건인증코드) 
        // 2-3. redirect_uri(), 
        // 2-4. state(NaverToken에 구현방식잇음), 
    // 3. 해당 내용을 json화 시켜서 
    // https://nid.naver.com/oauth2.0/token?client_id={클라이언트 아이디}&client_secret={클라이언트 시크릿}&grant_type=authorization_code&state={상태 토큰}&code={인증 코드}
    //로 보냄
}
