package community.mingle.app.src.member.model;

import lombok.Getter;

@Getter
public class SendPushNotificationRequest {
    private Long postId; //알림 보낼 게시물 id
    private String title; //알림 제목
    private String body; //알림 내용
}
