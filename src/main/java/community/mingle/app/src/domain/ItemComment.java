package community.mingle.app.src.domain;

import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalCommentLike;
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

    @OneToMany(mappedBy = "itemComment")
    private List<ItemCommentLike> itemCommentLikes = new ArrayList<>();

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

    public void deleteItemComment() {
        this.deletedAt = LocalDateTime.now();
        this.status = PostStatus.INACTIVE;
    }


//    @OneToMany(mappedBy = "item_comment")
//    private List<ItemComment> itemCommentLikes = new ArrayList<>();

    public static ItemComment createComment(Item item, Member member, String content, Long parentCommentId, Long mentionId, boolean isAnonymous, Long anonymousId) {
        ItemComment itemComment = new ItemComment();
        itemComment.setItem(item);
        itemComment.setMember(member);
        itemComment.setContent(content);
        itemComment.setParentCommentId(parentCommentId);
        itemComment.setMentionId(mentionId);
        itemComment.setAnonymous(isAnonymous);
        itemComment.setAnonymousId(anonymousId);
        itemComment.createdAt = LocalDateTime.now();
        itemComment.updatedAt = LocalDateTime.now();
        itemComment.status = PostStatus.ACTIVE;

        return itemComment;
    }
}
