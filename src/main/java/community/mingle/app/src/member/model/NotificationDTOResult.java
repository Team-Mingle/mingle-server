package community.mingle.app.src.member.model;


import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.Category;
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
import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
@AllArgsConstructor
public class NotificationDTOResult {

    private Long notificationId;
//    private Long memberId;
    private NotificationType notificationType;
    private String content;
    private BoardType boardType;
    private String category;
    private boolean isRead;

//    private String createdTime;


    public NotificationDTOResult(NotificationDTO t) {
        this.notificationId= t.getNotificationId();
//        this.memberId = t.getMemberId();

        if (t.getBoardType().equals(BoardType.광장)) {
            if (t.getTotalComment().isPresent()) {
                this.content = t.getTotalComment().get().getContent();//댓글
            } else {
                this.content = t.getTotalPost().getTitle(); //인기게시물
            }
        }
        else if (t.getBoardType().equals(BoardType.잔디밭)) {
            if (t.getUnivComment().isPresent()) { //댓글
                this.content = t.getUnivComment().get().getContent();
            } else {
                this.content = t.getUnivPost().getTitle();   //인기게시물
            }
        }
        this.notificationType= t.getNotificationType();
        this.boardType = t.getBoardType();
        this.category = t.getCategory
        this.isRead = t.isRead();
//        this.createdTime =convertLocaldatetimeToTime(t.getCreatedTime());

    }


}

