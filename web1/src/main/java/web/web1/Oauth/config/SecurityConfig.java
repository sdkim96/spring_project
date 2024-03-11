package web.web1.Oauth.config;
import web.web1.Oauth.domain.OAuth2MemberService;
import web.web1.Oauth.domain.OAuth2TokenService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import web.web1.Oauth.config.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2MemberService oAuth2MemberService;
    private final OAuth2TokenService oAuth2TokenService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable() // CSRF 비활성화
            .cors().and()
            .authorizeRequests()
                .requestMatchers("/private/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
            .and()
                .logout()
                .logoutSuccessUrl("/loginForm?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()
                .oauth2Login()
                .loginPage("http://localhost:3000/login")
                .defaultSuccessUrl("http://localhost:3000/")
                .userInfoEndpoint()
                .userService(oAuth2MemberService)
                .and()
                .successHandler(new CustomAuthenticationSuccessHandler(oAuth2TokenService));

        return http.build();
    }

    private LogoutSuccessHandler customLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("http://localhost:3000/login");
        };
    }
}

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// // import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// // import org.springframework.security.web.SecurityFilterChain;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


// @Configuration
// @RequiredArgsConstructor
// @EnableWebSecurity
// public class SecurityConfig {
//     private final OAuth2MemberService oAuth2MemberService;
//     private final OAuth2TokenService oAuth2TokenService;

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .httpBasic().disable()
//             .csrf().disable()
//             .cors().and()
//             .authorizeRequests()
//                 .requestMatchers("/private/**").authenticated()
//                 .requestMatchers("/admin/**").hasRole("ADMIN")
//                 .anyRequest().permitAll()
//             .and()
//                 .formLogin()
//                 .loginPage("/loginForm")
//                 .loginProcessingUrl("/login")
//                 .defaultSuccessUrl("/home", true)
//             .and()
//                 .logout()
//                 .logoutSuccessUrl("/loginForm?logout") // 로그아웃 성공 시 리디렉션될 URL
//                 .invalidateHttpSession(true) // HTTP 세션 무효화
//                 .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
//                 .permitAll()
//             .and()
//                 .oauth2Login()
//                 .loginPage("http://localhost:3000/login")
//                 .defaultSuccessUrl("http://localhost:3000/")
//                 .userInfoEndpoint()
//                 .userService(oAuth2MemberService);




//         return http.build();
//     }

//     // 로그아웃 성공 핸들러 구현이 필요한 경우 여기에 추가합니다.
//     // 예: 커스텀 로그아웃 성공 핸들러
//     // .logoutSuccessHandler(customLogoutSuccessHandler())

//     private LogoutSuccessHandler customLogoutSuccessHandler() {
//         return (request, response, authentication) -> {
//             // 커스텀 로그아웃 성공 로직 구현
//             response.sendRedirect("http://localhost:3000/login");
//         };
//     }
// }


// // @Configuration
// // @RequiredArgsConstructor
// // @EnableWebSecurity
// // public class SecurityConfig {
// //     private final OAuth2MemberService oAuth2MemberService;

// //     @Bean
// //     public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
// //         return httpSecurity
// //                 .httpBasic().disable()
// //                 .csrf().disable()
// //                 .cors().and()
// //                 .authorizeRequests()
// //                 .requestMatchers("/private/**").authenticated() //private로 시작하는 uri는 로그인 필수
// //                 .requestMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") //admin으로 시작하는 uri는 관릴자 계정만 접근 가능
// //                 .anyRequest().permitAll() //나머지 uri는 모든 접근 허용
// //                 .and()
// //                 .formLogin() // form login 관련 설정
// //                 .loginPage("/loginForm")
// //                 .usernameParameter("name") // Member가 username이라는 파라미터 갖고 있으면 안 적어도 됨.
// //                 .loginProcessingUrl("/login") // 로그인 요청 받는 url
// //                 .defaultSuccessUrl("/home") // 로그인 성공 후 이동할 url
// //                 .and().oauth2Login()//oauth2 관련 설정
// //                 .loginPage("/loginForm") //로그인이 필요한데 로그인을 하지 않았다면 이동할 uri 설정
// //                 .defaultSuccessUrl("http://localhost:3000/") //OAuth 구글 로그인이 성공하면 이동할 uri 설정
// //                 .userInfoEndpoint()//로그인 완료 후 회원 정보 받기
// //                 .userService(oAuth2MemberService).and().and().build(); //
// //                 .logout((logout) -> logout.logoutUrl)
// //                     .logoutSuccessUrl("http://localhost:3000/login/")
// //                     .invalidateHttpSession(true) // 세션 무효화
// //                     .clearAuthentication(true) // 인증 정보 제거
// //                     .and();

// //     }
// // }