package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.*;
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
@Table(name = "univ_notification")
public class UnivNotification {

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
    private UnivPost univPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private UnivComment univComment;


    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", name = "notification_type")
    private NotificationType notificationType;


    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", name = "board_type")
    private BoardType boardType;

    @Column(name = "is_read")
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private CategoryType category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static UnivNotification saveUnivNotification(UnivPost univPost, Member member, UnivComment univComment){
    UnivNotification univNotification = new UnivNotification();
    univNotification.setMember(member);
    univNotification.tableId = 2;
    univNotification.setUnivPost(univPost);
    univNotification.setUnivComment(univComment);
    univNotification.createdAt = LocalDateTime.now();
    univNotification.isRead = false;
    univNotification.boardType = BoardType.잔디밭;
    univNotification.notificationType = NotificationType.댓글;
    univNotification.category = CategoryType.valueOf(univPost.getCategory().getName());
    return univNotification;
    }

    public static UnivNotification saveUnivTotalNotification(UnivPost univpost, Member member){
    UnivNotification univNotification=new UnivNotification();
    univNotification.setMember(member);
    univNotification.tableId = 2;
    univNotification.setUnivPost(univpost);
    univNotification.setUnivComment(null);
    univNotification.createdAt = LocalDateTime.now();
    univNotification.isRead = false;
    univNotification.boardType = BoardType.잔디밭;
    univNotification.notificationType = NotificationType.인기;
    univNotification.category = CategoryType.valueOf(univpost.getCategory().getName());
    return univNotification;

    }


    public void readNotification(){
    this.isRead=true;

    }


}
