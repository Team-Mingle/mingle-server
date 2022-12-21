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
import java.util.Objects;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
@AllArgsConstructor
public class NotificationDTOResult {

    private Long notificationIdx;
    private Long memberIdx;
    private String content;
    private NotificationType notificationType;
    private BoardType boardType;
    private boolean isRead;
    private String createdTime;


    public NotificationDTOResult(NotificationDTO t) {
        this.notificationIdx= t.getNotificationIdx();
        this.memberIdx = t.getMemberIdx();

        if (t.getBoardType().equals(BoardType.광장)) {
            if (t.getTotalComment().isPresent()) {
                this.content = t.getTotalComment().get().getContent();//댓글
            } else {
                this.content = t.getTotalPost().getTitle(); //인기게시물
            }
        }
        else if (t.getBoardType().equals(BoardType.잔디밭)) {
            System.out.println("실행");
            if (t.getUnivComment().isPresent()) { //댓글
                this.content = t.getUnivComment().get().getContent();
            } else {
                this.content = t.getUnivPost().getTitle();   //인기게시물
            }
        }

//        if (boardType.equals(BoardType.광장)) {
//            if (Objects.isNull(t.getTotalComment())) {
//                this.content = t.getTotalComment().getContent();//댓글
//                this.content = ofNullable(t.getTotalComment().orElse(null));
//            } else {
//                this.content = t.getTotalPost().getTitle(); //인기게시물
//            }
//        }

//        else if (boardType.equals(BoardType.잔디밭)) {
////            if (t.getUnivComment() != null) { //댓글
//            if (Objects.isNull(t.getUnivComment())) {
//                this.content = t.getUnivComment().get();
//            } else {
//                this.content = t.getUnivPost().getTitle();   //인기게시물
//            }
//        }

        this.notificationType= t.getNotificationType();
        this.boardType = t.getBoardType();
        this.isRead = t.isRead();
        this.createdTime =convertLocaldatetimeToTime(t.getCreatedTime());

    }


}

