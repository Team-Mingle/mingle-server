package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
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
@Table(name="univ_post_scrap")

public class UnivPostScrap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univpost_scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univpost_id")
    private UnivPost univPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public static UnivPostScrap scrapUnivPost(UnivPost univPost, Member member) {
        List<UnivPostScrap> univPostScrapList = member.getUnivPostScraps();
        if (univPostScrapList == null || univPostScrapList.isEmpty()) {
        }
        else {
            for (UnivPostScrap univPostScrap : univPostScrapList) {
                if (Objects.equals(univPostScrap.getUnivPost().getId(), univPost.getId())) {
                    return null;
                }
            }
        }
        UnivPostScrap univPostScrap = new UnivPostScrap();
        univPostScrap.setMember(member);
        univPostScrap.setUnivPost(univPost);
        univPostScrap.createdAt = LocalDateTime.now();

        return univPostScrap;

    }
}
