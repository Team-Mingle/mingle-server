package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.NotificationType;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivNotification;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
@AllArgsConstructor
public class NotificationDTO { //noti //univnoti //totalnoti

    private Long notificationId;
    private Long memberId;
    //post en
    private TotalPost totalPost;
    private UnivPost univPost;
    private int tableId;
    private Optional<UnivComment> univComment;
    private Optional<TotalComment> totalComment;
    private NotificationType notificationType;
    private BoardType boardType;
    private boolean isRead;
    private LocalDateTime createdTime;


    public NotificationDTO(UnivNotification n) {
        this.notificationId = n.getId();
        this.tableId = n.getTableId();
        this.memberId = n.getMember().getId();
        this.univPost = n.getUnivPost();
        this.univComment = Optional.ofNullable(n.getUnivComment()); // <-여기 id 가 없을수도 있음. 그리고 커멘트 본문을 보여줘야함
        this.notificationType = n.getNotificationType();
        this.boardType = n.getBoardType();
        this.isRead = n.getIsRead();
        this.createdTime = n.getCreatedAt();

    }

    public NotificationDTO(TotalNotification t) {
        this.notificationId = t.getId();
        this.tableId = t.getTableId();
        this.memberId = t.getMember().getId();
        this.totalPost = t.getTotalPost();
        this.totalComment = Optional.ofNullable(t.getTotalComment());
        this.notificationType = t.getNotificationType();
        this.boardType = t.getBoardType();
        this.isRead = t.getIsRead();
        this.createdTime = t.getCreatedAt();

    }


}

