package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(name = "mention_id")
    private Long mentionId;

    @Column(name = "anonymous_id")
    private Long anonymousId;


    /**
     * 양방향 3.10 추가
     */
    @OneToMany(mappedBy = "univComment")
    private List<UnivCommentLike> univCommentLikes = new ArrayList<>();

    /**알림 */
    @OneToMany(mappedBy = "univComment")
    private List<UnivNotification> univNotifications = new ArrayList<>();



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

    public void modifyStatusAsReported() {
        this.status = PostStatus.REPORTED;
    }

    public void modifyStatusAsNotified() {
        this.status = PostStatus.NOTIFIED;
    }

    public void modifyStatusAsDeleted() {
        this.status = PostStatus.DELETED;
    }

//    public void modifyInactiveStatus() {
//        this.status = PostStatus.INACTIVE; //update 문으로 변경
//    }

    public static UnivComment createComment(UnivPost univPost, Member member, String content, Long parentCommentId, Long mentionId, boolean isAnonymous, Long anonymousId) {
        UnivComment univComment = new UnivComment();
        univComment.setUnivPost(univPost);
        univComment.setMember(member);
        univComment.setContent(content);
        univComment.setParentCommentId(parentCommentId);
        univComment.setMentionId(mentionId);
        univComment.setAnonymous(isAnonymous);
        univComment.setAnonymousId(anonymousId);
        univComment.createdAt = LocalDateTime.now();
        univComment.updatedAt = LocalDateTime.now();
        univComment.status = PostStatus.ACTIVE;

        return univComment;
    }

    public void deleteUnivComment (){
        this.deletedAt = LocalDateTime.now();
        this.status = PostStatus.INACTIVE;
    }

}
