package web.web1.Member.config;

import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import web.web1.Member.domain.OAuth2MemberService;
import web.web1.Member.domain.OAuth2TokenService;

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
            .csrf(csrf -> csrf.disable()) 
            .cors().and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/member/logouts").permitAll() //로그아웃
                .requestMatchers("member/login").permitAll() //로그인
                .requestMatchers("/member/join").permitAll()
                .requestMatchers("/map/getaddress").permitAll() //주소검색
                .requestMatchers("/map/getgeocoding").permitAll() //좌표검색
                .requestMatchers("/memberpage/queryhistory").permitAll()
                .requestMatchers("/memberpage/userprofile").permitAll()
                .requestMatchers("/memberpage/userprofile/update").permitAll()
                .requestMatchers("/memberpage/recommend").permitAll()
                .requestMatchers("/user_profile_photos/**").permitAll()
                .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2 //OAuth2 로그인
                .userInfoEndpoint()
                    .userService(oAuth2MemberService)
                .and()
                    .successHandler(new CustomAuthenticationSuccessHandler(oAuth2TokenService)));
        return http.build();
    }
}