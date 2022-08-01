package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="univ_comment_like")

public class UnivCommentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univcomment_like_id")
    private Long id;

    /**
     * 포스트가 아니라 커멘트랑 조인해야지 정신나간 현우야
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univcomment_id")
    private UnivComment univComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;



}
