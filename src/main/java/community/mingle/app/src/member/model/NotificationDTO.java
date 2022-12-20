package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.NotificationType;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Univ.UnivNotification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
@AllArgsConstructor

public class NotificationDTO {

    private Long notificationIdx;
    private Long memberIdx;

    private Long postIdx;

    private Long commentIdx;

    private NotificationType notificationType;

    private BoardType boardType;

    private boolean isRead;

    private String createdTime;


    public NotificationDTO(UnivNotification n) {
        this.notificationIdx= n.getId();
        this.memberIdx = n.getMember().getId();
        this.postIdx = n.getUnivPost().getId();
        this.commentIdx= n.getUnivComment().getId();
        this.notificationType= n.getNotificationType();
        this.boardType = n.getBoardType();
        this.isRead = n.getIsRead();
        this.createdTime = convertLocaldatetimeToTime(n.getCreatedAt());



    }

    public NotificationDTO(TotalNotification t) {
        this.notificationIdx= t.getId();
        this.memberIdx = t.getMember().getId();
        this.postIdx = t.getTotalPost().getId();
        this.commentIdx= t.getTotalComment().getId();
        this.notificationType= t.getNotificationType();
        this.boardType = t.getBoardType();
        this.isRead = t.getIsRead();
        this.createdTime = convertLocaldatetimeToTime(t.getCreatedAt());

    }





}

