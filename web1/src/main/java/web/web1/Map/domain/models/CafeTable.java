package web.web1.Map.domain.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder // 이 위치에 @Builder 어노테이션을 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자를 자동으로 생성
@NoArgsConstructor // JPA 스펙에 의해 기본 생성자도 필요
public class CafeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // New auto-incrementing ID

    private String cafeName;

    private String province;
    private String cityDistrict;
    private String neighborhood;

    private double longitude;
    private double latitude;

}
