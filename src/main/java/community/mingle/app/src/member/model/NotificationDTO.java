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
public class NotificationDTO {

    private Long notificationIdx;

    private Long memberIdx;
    //post en
    private Long postIdx;


    private int tableId;

    //comment
    private Long commentIdx;
    private String content;

    private NotificationType notificationType;

    private BoardType boardType;

    private boolean isRead;

    private LocalDateTime createdTime;


    public NotificationDTO(UnivNotification n) {
        this.notificationIdx= n.getId();
        this.tableId = n.getTableId();
        this.memberIdx = n.getMember().getId();
        this.postIdx = n.getUnivPost().getId();
        this.commentIdx= n.getUnivComment().getId(); // <-여기 id 가 없을수도 있음. 그리고 커멘트 본문을 보여줘야함
        this.notificationType= n.getNotificationType();
        this.boardType = n.getBoardType();
        this.isRead = n.getIsRead();
        this.createdTime = n.getCreatedAt();



    }

    public NotificationDTO(TotalNotification t) {
        this.notificationIdx= t.getId();
        this.tableId = t.getTableId();
        this.memberIdx = t.getMember().getId();
        this.postIdx = t.getTotalPost().getId();
        this.commentIdx = t.getTotalComment().getId();
        this.notificationType= t.getNotificationType();
        this.boardType = t.getBoardType();
        this.isRead = t.getIsRead();
        this.createdTime = t.getCreatedAt();

    }





}

