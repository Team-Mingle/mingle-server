package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
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
@Table(name="total_post_scrap")

public class TotalPostScrap {

    private static int postIdx;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalpost_scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "totalpost_id")
    private TotalPost totalPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public static TotalPostScrap scrapTotalPost(TotalPost totalpost, Member member) {
        List<TotalPostScrap> totalPostScraps = member.getTotalPostScraps();
        if (totalPostScraps == null || totalPostScraps.isEmpty()) {
        }
        else {
            for (TotalPostScrap totalPostScrap : totalPostScraps) {
                if (Objects.equals(totalPostScrap.getTotalPost().getId(), totalpost.getId())) {
                    return null;
                }
            }
        }
        TotalPostScrap totalPostScrap = new TotalPostScrap();
        totalPostScrap.setMember(member);
        totalPostScrap.setTotalPost(totalpost);
        totalPostScrap.createdAt = LocalDateTime.now();

        return totalPostScrap;

    }


}