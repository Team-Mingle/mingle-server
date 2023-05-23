package community.mingle.app.src.domain;

import community.mingle.app.src.domain.Total.TotalPost;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="item_blind")
public class ItemBlind {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_blind_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static ItemBlind blindItem(Item item, Member member) {
        List<ItemBlind> itemBlindList = member.getItemBlind();
        if (itemBlindList == null || itemBlindList.isEmpty()) {
        } else {
            for (ItemBlind itemBlind : itemBlindList) {
                if (Objects.equals(itemBlind.getItem().getId(), item.getId())) {
                    return null;
                }
            }
        }
        ItemBlind itemBlind = new ItemBlind();
        itemBlind.setMember(member);
        itemBlind.setItem(item);
        itemBlind.createdAt = LocalDateTime.now();
        return itemBlind;
    }
}
