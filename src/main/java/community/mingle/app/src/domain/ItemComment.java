package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "mention_id")
    private Long mentionId;

    /** 익명방법? */
    @Column(name = "is_anonymous")
    private boolean isAnonymous;

    @Column(name = "anonymous_id")
    private Long anonymousId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private PostStatus status;

//    @OneToMany(mappedBy = "item_comment")
//    private List<ItemComment> itemCommentLikes = new ArrayList<>();
}
