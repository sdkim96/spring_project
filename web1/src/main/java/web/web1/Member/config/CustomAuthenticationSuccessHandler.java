package web.web1.Member.config;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import web.web1.Member.domain.OAuth2TokenService;
import web.web1.Member.domain.PrincipalDetails;


// 아래 클래스는 Security Config의 OAuth2 로그인 성공 핸들러를 커스터마이징한 클래스입니다.
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2TokenService oAuth2TokenService;

    public CustomAuthenticationSuccessHandler(OAuth2TokenService oAuth2TokenService) {
        this.oAuth2TokenService = oAuth2TokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            
            // PrincipalDetails에서 provider를 가져옴(구글)
            String serviceType = principalDetails.getMember().getProvider();

            // URL에서 code를 가져옴
            String code = request.getParameter("code");
            
            // code는 기본적으로 코딩되있는 상황이라 decode를 해야함
            String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);

            if (decode != null && !decode.isEmpty()) {
                
                // OAuth2TokenService의 makeJwtToken 메소드를 사용하여 액세스 토큰을 생성 (JWT 토큰 생성)
                String accessToken = oAuth2TokenService.makeJwtToken(principalDetails.getMember(), serviceType, decode);

                // 엑세스 토큰을 콘솔에 출력
                System.out.println("Access Token: " + accessToken);

                // HTTP-only 쿠키에 액세스 토큰 저장
                // 원래는 해당 서비스에 코드를 전송해야하지만, 한계에 부딪혀 우리 웹서비스 jwt 토큰으로 저장함
                Cookie cookie = new Cookie("token", accessToken);
                System.out.println("Cookie: " + cookie);
                cookie.setHttpOnly(false);
                cookie.setPath("/");
                cookie.setSecure(false); // HTTPS 환경에서만 쿠키를 전송
                response.addCookie(cookie);

                response.sendRedirect("http://localhost:3000/");
                clearAuthenticationAttributes(request);
                    
            } else {
                // code가 없는 경우
                super.onAuthenticationSuccess(request, response, authentication);
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
