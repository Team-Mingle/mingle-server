package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalBlind;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostLike;
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
@Table(name="univ_blind")

public class UnivBlind {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univ_blind_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univpost_id")
    private UnivPost univPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static UnivBlind blindUnivPost(UnivPost univpost, Member member) {
        List<UnivBlind> univBlindList = member.getUnivBlindPost();
        if (univBlindList == null || univBlindList.isEmpty()) {
        } else {
            for (UnivBlind univBlind : univBlindList) {
                if (Objects.equals(univBlind.getUnivPost().getId(), univpost.getId())) {
                    return null;
                }
            }
        }
        UnivBlind univBlind = new UnivBlind();
        univBlind.setMember(member);
        univBlind.setUnivPost(univpost);
        univBlind.createdAt = LocalDateTime.now();
        return univBlind;
    }


}
