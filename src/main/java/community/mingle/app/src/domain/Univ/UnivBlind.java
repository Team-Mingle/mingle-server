package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
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

//
//    public static UnivPostLike likesUnivPost(UnivPost univPost, Member member) {
//        List<UnivPostLike> univPostLikeList = member.getUnivPostLikes();
//        if (univPostLikeList == null || univPostLikeList.isEmpty()) {
//        }
//        else {
//            for (UnivPostLike UnivPostLike : univPostLikeList) {
//                if (Objects.equals(UnivPostLike.getUnivPost().getId(), univPost.getId())) {
//                    return null;
//                }
//            }
//        }
//        UnivPostLike univPostLike = new UnivPostLike();
//        univPostLike.setMember(member);
//        univPostLike.setUnivPost(univPost);
//        univPostLike.createdAt = LocalDateTime.now();
//        return univPostLike;
//    }
//

}
