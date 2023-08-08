package community.mingle.app.src.member.model;


import community.mingle.app.src.domain.BoardType;
import community.mingle.app.src.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
@AllArgsConstructor
public class NotificationDTOResult {

    private Long notificationId;
    private NotificationType notificationType;
    private Long postId;
    private String content;
    private String category;
    private BoardType boardType;
    private boolean isRead;
    private String createdAt;


    public NotificationDTOResult(NotificationDTO t) {
        //default 값 비어있는 string 으로
        this.notificationId = t.getNotificationId();
        if (t.getBoardType().equals(BoardType.광장)) {
            if (t.getReportedPostId() != null) {
                this.postId = t.getReportedPostId();
                this.content = t.getReportMessage();
                this.category = t.getCategory().name();
            }
            else{
                this.postId = t.getTotalPost().getId();
                this.category = t.getCategory().name();
                if (t.getTotalComment().isPresent()) {
                    this.content = t.getTotalComment().get().getContent();//댓글
                } else {
                    this.content = t.getTotalPost().getTitle(); //인기게시물
                }
            }
        }
        else if (t.getBoardType().equals(BoardType.잔디밭)) {
            if (t.getReportedPostId() != null) {
                this.postId = t.getReportedPostId();
                this.content = t.getReportMessage();
                this.category = t.getCategory().name();
            }
            else{
                this.postId = t.getUnivPost().getId();
                this.category = t.getCategory().name();
                if (t.getUnivComment().isPresent()) { //댓글
                    this.content = t.getUnivComment().get().getContent();
                } else {
                    this.content = t.getUnivPost().getTitle();   //인기게시물
                }
            }
        } else if (t.getBoardType().equals(BoardType.장터)) {
            this.postId = t.getItem().getId();
            if (t.getItemComment().isPresent()) {
                this.content = t.getItemComment().get().getContent();
            }
            this.category = "";
        }
        this.notificationType= t.getNotificationType();
        this.boardType = t.getBoardType();
        this.isRead = t.isRead();
        this.createdAt = convertLocaldatetimeToTime(t.getCreatedAt());
    }


}

