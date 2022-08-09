package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
@Setter
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="total_comment_like")

public class TotalCommentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalcomment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "totalcomment_id")
    private TotalComment totalComment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;



    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static TotalCommentLike likesTotalComment(TotalComment totalcomment, Member member) {
        TotalCommentLike totalCommentLike = new TotalCommentLike();
        totalCommentLike.setMember(member);
        totalCommentLike.setTotalComment(totalcomment);
        totalCommentLike.createdAt = LocalDateTime.now();

        return totalCommentLike;
    }






}
