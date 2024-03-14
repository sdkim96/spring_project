package web.web1.Config;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Embeddable;

// MemberHistory 엔티티의 복합키를 정의한 클래스
@Embeddable
public class MemberHistoryId implements Serializable {
    private String email;
    private LocalDateTime searchDateTime;

    public MemberHistoryId() {
    }

    public MemberHistoryId(String email, LocalDateTime searchDateTime) {
        this.email = email;
        this.searchDateTime = searchDateTime;
    }

    // getters and setters
}