package community.mingle.app.src.domain.Total;


import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostLike;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="total_blind")

public class TotalBlind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalblind_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "totalpost_id")
    private TotalPost totalPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static TotalBlind blindTotalPost(TotalPost totalpost, Member member) {
        List<TotalBlind> totalBlindList = member.getTotalBlindPost();
        if (totalBlindList == null || totalBlindList.isEmpty()) {
        } else {
            for (TotalBlind totalBlind : totalBlindList) {
                if (Objects.equals(totalBlind.getTotalPost().getId(), totalBlind.getId())) {
                    return null;
                }
            }
        }
        TotalBlind totalBlind = new TotalBlind();
        totalBlind.setMember(member);
        totalBlind.setTotalPost(totalpost);
        totalBlind.createdAt = LocalDateTime.now();
        return totalBlind;
    }

}
