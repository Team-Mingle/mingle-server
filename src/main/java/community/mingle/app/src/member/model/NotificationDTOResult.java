package community.mingle.app.src.member.model;


import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.NotificationType;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Univ.UnivNotification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
@AllArgsConstructor
public class NotificationDTOResult {

    private Long notificationIdx;
    private Long memberIdx;

    private Long postIdx;

    private Long commentIdx;

    private NotificationType notificationType;

    private BoardType boardType;

    private boolean isRead;

    private String createdTime;

    public NotificationDTOResult(NotificationDTO t) {
        this.notificationIdx= t.getNotificationIdx();
        this.memberIdx = t.getMemberIdx();
        this.postIdx = t.getPostIdx();
        this.commentIdx = t.getCommentIdx();
        this.notificationType= t.getNotificationType();
        this.boardType = t.getBoardType();
        this.isRead = t.isRead();
        this.createdTime =convertLocaldatetimeToTime(t.getCreatedTime());

    }

}

