package community.mingle.app.src.member.model;

import lombok.Getter;

@Getter
public class NotificationRequest {
    private int tableId; // 1 = total , 2 = univ
    private Long notificationId;
}
