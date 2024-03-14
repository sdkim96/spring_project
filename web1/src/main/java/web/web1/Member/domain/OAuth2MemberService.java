package web.web1.Member.domain;

import lombok.RequiredArgsConstructor;
import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.oauth2.*;
import web.web1.Member.domain.repository.MemberRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import web.web1.Config.*;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {
    private final BCryptPasswordEncoder encoder;
    private final MemberRepository memberRepository;

    // 기존 Oauth2User의 loadUser 메소드를 오버라이딩하여 구글 로그인을 처리하는 메소드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        System.out.println("---------------추출 도구 준비시작-----------------");

        // oAuth2User는 구글 로그인을 통해 받아온 회원 정보를 담고 있음
        // memberInfo는 회원 정보에서 정보를 추출해낼 수 있는 메소드를 제공
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2MemberInfo memberInfo = null;
        System.out.println("---------------추출 도구 준비완료-----------------");

        System.out.println("---------------추출 시작-----------------");

        // registrationId는 구글 로그인을 통해 받아온 회원 정보의 공급자를 나타냄
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("공급자명 = " + registrationId);
        
        // 만약 공급자가 구글이면 구글템플릿
        if (registrationId.equals("google")) {
            memberInfo = new GoogleMemberInfo(oAuth2User.getAttributes());
        } else { // 공급자가 다른경우면 (사실상 현재 서비스엔 해당없음)
            System.out.println("로그인 실패");
        }

        // 엔티티에 해당하는 변수와 값을 memberinfo에서 추출후 변수에 저장
        String provider = memberInfo.getProvider();
        String providerId = memberInfo.getProviderId();
        String oauth2Id = provider + "_" + providerId;
        String username = memberInfo.getName();
        String email = memberInfo.getEmail();
        String role;

        if (AdminConfig.ADMIN.contains(memberInfo.getEmail())) {
            System.out.println("관리자 계정입니다.");
            role = "ADMIN";
        } else {
            System.out.println("일반 사용자 계정입니다.");
            role = "USER";
        }

        System.out.println("---------------추출 완료-----------------");

        System.out.println("---------------회원 정보 저장 시작-----------------");
        // 추출한 정보를 토대로 회원 정보를 저장
        Optional<Member> findMember = memberRepository.findByOauth2Id(oauth2Id);
        Member member=null;
        if (findMember.isEmpty()) { //찾지 못했다면
            member = Member.builder()
                    .oauth2Id(oauth2Id)
                    .name(username)
                    .email(email)
                    .password(encoder.encode("password"))
                    .role(role)
                    .provider(provider)
                    .providerId(providerId).build();
            memberRepository.save(member);
        }
        else{
            member=findMember.get();
        }
        System.out.println("---------------회원 정보 저장 완료-----------------");
        
        PrincipalDetails principaldetails = new PrincipalDetails(member, oAuth2User.getAttributes());
        System.out.println("principaldetails = " + principaldetails);
        return principaldetails;
    }
}
