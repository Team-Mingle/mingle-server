package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="total_comment_like")

public class TotalCommentLike {

    @Id @GeneratedValue
    @Column(name = "totalcomment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "totalpost_id")
    private TotalPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
