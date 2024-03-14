package web.web1.Map.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.io.Serializable;

import web.web1.Config.MemberHistoryId;
import web.web1.Member.domain.models.Member;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MemberHistory {

    @EmbeddedId
    private MemberHistoryId id;

    private String searchQuery;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("email") // 복합 키의 일부를 외래 키로 매핑
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Member member;


    public MemberHistory(String searchQuery, Member member, LocalDateTime searchDateTime) {
        this.id = new MemberHistoryId(member.getEmail(), searchDateTime);
        this.searchQuery = searchQuery;
        this.member = member;
    }

    // getters and setters
}