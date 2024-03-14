package web.web1.Member.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Random;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import web.web1.Config.AdminConfig;
import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.repository.MemberRepository;
import web.web1.Member.domain.repository.TokenRepository;
import web.web1.Member.domain.token.AbstractTokenFactory;
import web.web1.Member.domain.token.GoogleTokenFactory;
import web.web1.Member.domain.token.NormalTokenFactory;
import web.web1.Member.domain.token.TokenFactory;


@Controller
@RequiredArgsConstructor
public class MemberController {
    private final BCryptPasswordEncoder encoder;
    private final MemberRepository memberRepository;

    @Autowired
    private GoogleTokenFactory googleTokenFactory;

    @Autowired
    private NormalTokenFactory normalTokenFactory;

    @Autowired
    private TokenRepository tokenRepository;

    private AbstractTokenFactory abstractTokenFactory;

    @Autowired
    private ApplicationContext applicationContext;

    private Member member;

    // 일반 로그인(소셜로그인x)
    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestBody Member member, HttpServletResponse response) {
        System.out.println("-----------------일반 로그인 시작-----------------");
        System.out.println("member = " + member);

        String email = member.getEmail();
        String rawPwd = member.getPassword();

        System.out.println("--------유저 정보--------");
        System.out.println(email);
        System.out.println(rawPwd);
        System.out.println("------------------------");


        
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isEmpty()) {
            System.out.println("가입되지 않은 계정입니다.");
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "가입되지 않은 계정입니다."));
        } else {
            Member memberInfo = findMember.get();
            if (encoder.matches(rawPwd, memberInfo.getPassword())) {
                System.out.println("로그인 성공");
                
                final String code = "ahweafhkjsbf234234423";
                String jwt =  normalTokenFactory.createToken(memberInfo, code);
                System.out.println("jwt = " + jwt);

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Authorization", "Bearer " + jwt);
                return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(Map.of(
                        // "role", memberInfo.getRole(),
                        // "email", memberInfo.getEmail(),
                        // "name", memberInfo.getName(),
                        // "provider", memberInfo.getProvider(),
                        // "providerId", memberInfo.getProviderId(),
                        // "oauth2Id", memberInfo.getOauth2Id(),
                        "message", "일반 로그인 성공"
                    ));

            } else {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }
        }
    }


    // 일반 회원가입(소셜로그인x)
    @PostMapping("/member/join")
    public ResponseEntity<?> join(@RequestBody Member member, HttpServletResponse response) {
        String rawPwd = member.getPassword();
        System.out.println("member = " + member);

        // 이미 가입된 이메일인지 확인
        // Optional<Member>는 Member 객체가 존재할 수도 있고, 존재하지 않을 수도 있음을 나타냅니다
        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
        if (existingMember.isPresent()) {
            System.out.println("이미 가입된 계정입니다.");
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "이미 가입된 계정입니다."));
        }

        // 이메일이 관리자 리스트에 있는지 확인하고, 그에 따라 역할을 설정합니다.
        if (AdminConfig.ADMIN.contains(member.getEmail())) {
            System.out.println("관리자 계정입니다.");
            member.setRole("ADMIN");
        } else {
            System.out.println("일반 사용자 계정입니다.");
            member.setRole("USER");
        }

        // 난수를 설정하고 providerID와 Oauth2ID 뒤에 붙입니다.
        Random random = new Random();
        int randomInt = random.nextInt(100000);
        member.setProvider("web1");
        member.setProviderId("web1" + randomInt);
        member.setOauth2Id("web1" + randomInt);

        // 비밀번호를 암호화하여 저장합니다.
        member.setPassword(encoder.encode(rawPwd));
        memberRepository.save(member);

        System.out.println("--------회원가입 정보--------");
        System.out.println("OAuth2 ID: " + member.getOauth2Id());
        System.out.println("Name: " + member.getName());
        System.out.println("Password: " + member.getPassword());
        System.out.println("Email: " + member.getEmail());
        System.out.println("Role: " + member.getRole());
        System.out.println("Provider: " + member.getProvider());
        System.out.println("Provider ID: " + member.getProviderId());
        System.out.println("----------------------------");

        // 성공적으로 가입된 경우 리다이렉션
        return ResponseEntity.ok(Map.of("message", "아이디 등록완료"));
    }

    // 로그아웃 시나리오
    @PostMapping("/member/logouts")
    public ResponseEntity<?> logout(HttpServletRequest request, @RequestBody Map<String, String> body) {
        System.out.println("--------로그아웃 시작--------");

        // Authorization 헤더에서 토큰 값 추출
        String authorizationHeader = request.getHeader("Authorization");
        String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
        System.out.println("토큰값 = " + tokenValue);

        // 요청 본문에서 provider 추출
        String provider = body.get("provider");
        System.out.println("제공업체 = " + provider);

        //checkToken의 세번째 매개변수(허용된 역할 리스트) -> 메소드마다 이 구문은 무조건 필수
        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

        // checkToken의 첫번째 매개변수(member 인스턴스 가져오기)
        TokenFactory tokenFactory = applicationContext.getBean(provider + "TokenFactory", TokenFactory.class);
        Map<String, Object> decodedToken = tokenFactory.decodeToken(tokenValue);
        String oauth2ID =  String.valueOf(decodedToken.get("oauth2Id"));

        // 추상클래스를 통해 구현된 토큰팩토리를 가져옵니다.
        

        // Member member, String tokenValue, List allowedRoles
        // 토큰 유효성 검사
    
        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));

        System.out.println("토큰이 유효합니까? " + isChecked);
        System.out.println("토큰이 만료되었습니까? " + isExpired);

        if (!isChecked) { //토큰이 유효하지 않으면 삭제
            tokenFactory.deleteToken(oauth2ID);
            System.out.println("토큰이 유효하지 않아 삭제되었습니다.");
            return ResponseEntity.ok()
                    .body(Map.of(
                        "message", "로그아웃 성공"
                    ));
        } else {
            // 토큰이 유효해도 만료와 상관없이 로그아웃 로직이므로 여전히 삭제
            tokenFactory.deleteToken(oauth2ID);
            System.out.println("로그아웃을 위해 토큰이 삭제되었습니다.");
            return ResponseEntity.ok()
                    .body(Map.of(
                        "message", "로그아웃 성공"
                    ));
        }
    }

}