package web.web1.Oauth.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import web.web1.Oauth.domain.OAuth2TokenService;
import web.web1.Oauth.domain.PrincipalDetails;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2TokenService oAuth2TokenService;

    public CustomAuthenticationSuccessHandler(OAuth2TokenService oAuth2TokenService) {
        this.oAuth2TokenService = oAuth2TokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            // Extract the service type (provider) from PrincipalDetails
            String serviceType = principalDetails.getMember().getProvider();

            // Retrieve the authorization code from the request
            String code = request.getParameter("code");
            String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);

            if (decode != null && !decode.isEmpty()) {
                // Assuming the clientId, clientSecret, and redirectUri are known or retrieved from a secure place
                String clientId = "162422478014-k7801p5p52th4qqn4iur14452kg6aolh.apps.googleusercontent.com"; // Placeholder: Get actual clientId
                String clientSecret = "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"; // Placeholder: Get actual clientSecret
                String redirectUri = "http://localhost:8080/login/oauth2/code/google"; // Placeholder: Get actual redirectUri
                
                // Directly exchange the authorization code for an access token
                String accessToken = oAuth2TokenService.exchangeCodeForAccessToken(decode, clientId, clientSecret, redirectUri);

                // Log or process the accessToken as needed
                System.out.println("Access Token: " + accessToken);

                // Optionally, redirect the user to a specific URL after processing
                getRedirectStrategy().sendRedirect(request, response, "/post-login-success");
            } else {
                // Fallback to the default behavior if the code is not available
                super.onAuthenticationSuccess(request, response, authentication);
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
