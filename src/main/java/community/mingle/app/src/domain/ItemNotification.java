package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_notification")
public class ItemNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_comment_id")
    private ItemComment itemComment;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", name = "notification_type")
    private NotificationType notificationType; //거래게시판

    @Column(name = "is_read")
    private Boolean isRead;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ItemNotification saveItemNotification(Item item, Member itemMember, ItemComment comment) {
        ItemNotification itemNotification = new ItemNotification();
        itemNotification.setMember(itemMember);
        itemNotification.setItem(item);
        itemNotification.setItemComment(comment);
        itemNotification.isRead = false;
        itemNotification.createdAt = LocalDateTime.now();
        itemNotification.notificationType = NotificationType.거래;
        return itemNotification;
    }
}
