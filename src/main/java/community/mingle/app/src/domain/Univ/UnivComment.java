package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostCommentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class UnivComment {

    @Id @GeneratedValue
    @Column(name = "univcomment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univpost_id")
    private UnivPost univPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @Column(name="parent_comment_id")
    private Long parentCommentId;

    /** 익명방법? */
    private boolean isAnonymous;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private PostCommentStatus status;



}
