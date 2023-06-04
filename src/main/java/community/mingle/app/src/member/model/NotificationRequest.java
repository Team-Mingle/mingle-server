package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationRequest {
//    private int tableId; // 1 = total , 2 = univ
    private BoardType boardType;
    private Long notificationId;
}
