package community.mingle.app.src.domain.Total;

import community.mingle.app.config.BaseException;
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
@Table(name="total_post_like")

public class TotalPostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalpost_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "totalpost_id")
    private TotalPost totalPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;



    public static TotalPostLike likesTotalPost(TotalPost totalpost, Member member) {

        List<TotalPostLike> totalPostLikeList = member.getTotalPostLikes();
        if (totalPostLikeList == null || totalPostLikeList.isEmpty()) {
        }
        else { //좋아요 중복 방지
            for (TotalPostLike totalPostLike : totalPostLikeList) {
                if (Objects.equals(totalPostLike.getTotalPost().getId(), totalpost.getId())) {
                    return null;
                }
            }
        }
        TotalPostLike totalPostLike = new TotalPostLike();
        totalPostLike.setMember(member);
        totalPostLike.setTotalPost(totalpost);
        totalPostLike.createdAt = LocalDateTime.now();
        return totalPostLike;
    }
}







