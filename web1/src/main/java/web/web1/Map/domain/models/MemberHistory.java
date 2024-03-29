package web.web1.Map.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import web.web1.Member.domain.models.Member;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder // 이 위치에 @Builder 어노테이션을 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자를 자동으로 생성
public class MemberHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // New auto-incrementing ID

    private String searchQuery;

    @Column(nullable = false, length = 191) // 동일하게 적용
    private String email; // 유저의 이메일을 기본키로 사용

    private LocalDateTime searchDateTime; // Directly included in the entity

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private Member member;

    // getters and setters
}
