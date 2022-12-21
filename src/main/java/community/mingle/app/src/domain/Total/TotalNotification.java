package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "total_notification")
public class TotalNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "table_id")
    private int tableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private TotalPost totalPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private TotalComment totalComment;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum",  name = "notification_type")
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum" ,  name = "board_type")
    private BoardType boardType;

    @Column(name = "is_read")
    private Boolean isRead;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static TotalNotification saveTotalNotification(TotalPost totalPost, Member member, TotalComment totalComment){
        TotalNotification totalNotification = new TotalNotification();
        totalNotification.setMember(member);
        totalNotification.tableId = 1;
        totalNotification.setTotalPost(totalPost);
        totalNotification.setTotalComment(totalComment);
        totalNotification.createdAt = LocalDateTime.now();
        totalNotification.isRead = false;
        totalNotification.boardType = BoardType.광장;
        totalNotification.notificationType = NotificationType.댓글;
        return totalNotification;
    }
    public static TotalNotification saveTotalPostNotification(TotalPost totalpost, Member member){
        TotalNotification totalNotification = new TotalNotification();
        totalNotification.setMember(member);
        totalNotification.tableId = 1;
        totalNotification.setTotalPost(totalpost);
        totalNotification.createdAt = LocalDateTime.now();
        totalNotification.isRead = false;
        totalNotification.boardType = BoardType.광장;
        totalNotification.notificationType = NotificationType.인기;
        return totalNotification;
    }





    public void readNotification() {
        this.isRead = true;
    }


}
