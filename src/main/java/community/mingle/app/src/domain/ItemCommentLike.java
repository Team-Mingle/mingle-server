package community.mingle.app.src.domain;

import community.mingle.app.config.BaseException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static community.mingle.app.config.BaseResponseStatus.DUPLICATE_LIKE;

@Entity
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="item_comment_like")
public class ItemCommentLike {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_comment_id")
    private ItemComment itemComment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static ItemCommentLike likeItemComment(ItemComment itemComment, Member member) throws BaseException {
        List<ItemCommentLike> itemCommentLikes = itemComment.getItemCommentLikes();
        for (ItemCommentLike ItemCommentLike : itemCommentLikes) {
            if (ItemCommentLike.getMember().equals(member)) {
                throw new BaseException(DUPLICATE_LIKE); //중복
            }
        }
        ItemCommentLike itemCommentLike = new ItemCommentLike();
        itemCommentLike.setMember(member);
        itemCommentLike.setItemComment(itemComment);
        itemCommentLike.setCreatedAt(LocalDateTime.now());
        return itemCommentLike;
    }
    
}
