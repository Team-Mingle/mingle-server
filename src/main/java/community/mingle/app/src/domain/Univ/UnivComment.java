package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "anonymous_id")
    private Long anonymousId;

    /**
     * 양방향 3.10 추가
     */
    @OneToMany(mappedBy = "univComment")
    private List<UnivCommentLike> univCommentLikes = new ArrayList<>();


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

    public void modifyReportStatus() {
        this.status = PostStatus.REPORTED;
    }

    public void modifyInactiveStatus() {
        this.status = PostStatus.INACTIVE;
    }



}
