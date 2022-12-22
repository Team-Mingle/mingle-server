package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.BoardType;
import lombok.Getter;

@Getter
public class NotificationRequest {
//    private int tableId; // 1 = total , 2 = univ
    private BoardType boardType;
    private Long notificationId;
}
