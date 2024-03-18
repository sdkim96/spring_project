package web.web1.Member.domain.models;

import java.time.LocalDateTime;

import org.checkerframework.checker.units.qual.C;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder // 이 위치에 @Builder 어노테이션을 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자를 자동으로 생성
public class Token {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 191) // 동일하게 적용
    private String email; // 유저의 이메일을 기본키로 사용

    private String tokenValue;
    private LocalDateTime createdTime;
    private LocalDateTime expiredTime;
    private String tokenType;

    @OneToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private Member member;
}
