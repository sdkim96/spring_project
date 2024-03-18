package web.web1.Member.domain.models;

import jakarta.persistence.*;
import lombok.*;
import web.web1.Map.domain.models.MemberHistory;
import web.web1.Map.domain.models.Recommend;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @Column(length = 191) // MySQL/MariaDB의 UTF8MB4 인코딩에서 최대 인덱스 길이를 고려
    private String email; // 유저 구글 이메일을 기본키로 사용
    
    private String oauth2Id;
    private String name; //유저 이름
    private String password; //유저 비밀번호
    private String role; //유저 권한 (일반 유저, 관리자)
    private String provider; //공급자 (google, facebook ...)
    private String providerId; //공급 아이디
    private String photoPath; //프로필 이미지

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberHistory> userHistories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommend> userRecommed = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Token token;

    @Builder
    public Member(String oauth2Id, String name, String password, String email, String role, String provider, String providerId) {
        this.oauth2Id = oauth2Id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.photoPath = photoPath;
    }
}
