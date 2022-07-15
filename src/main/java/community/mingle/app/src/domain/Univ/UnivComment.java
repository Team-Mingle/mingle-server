package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="univ_comment")

public class UnivComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univcomment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univpost_id")
    private UnivPost univPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    /** 익명방법? */
    @Column(name = "is_anonymous")
    private boolean isAnonymous;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private PostStatus status;



}
