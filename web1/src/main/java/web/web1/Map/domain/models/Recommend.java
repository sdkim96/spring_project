package web.web1.Map.domain.models;

import jakarta.persistence.*;

import web.web1.Member.domain.models.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder // 이 위치에 @Builder 어노테이션을 추가
@AllArgsConstructor
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // New auto-incrementing ID

    @Column(nullable = false, length = 191) // 동일하게 적용
    private String email; // 유저의 이메일을 기본키로 사용

    private Double longitude;
    private Double latitude;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private Member member;
    
}
