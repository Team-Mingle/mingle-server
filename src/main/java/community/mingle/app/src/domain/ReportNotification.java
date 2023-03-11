package community.mingle.app.src.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "report_notification")
public class ReportNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum" , name = "report_type", nullable = false)
    private PostStatus reportType;

    @NotNull
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "is_read")
    private Boolean isRead;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", nullable = false)
    private BoardType boardType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private String categoryType;
    @NotNull
    @Column(columnDefinition = "enum" ,name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ReportNotification saveReportNotification(Long memberId, PostStatus postStatus, Long postId, BoardType boardType, NotificationType notificationType, String categoryType) {
        ReportNotification reportNotification = new ReportNotification();
        reportNotification.memberId = memberId;
        reportNotification.reportType = postStatus;
        reportNotification.postId = postId;
        reportNotification.isRead = false;
        reportNotification.boardType = boardType;
        reportNotification.createdAt = LocalDateTime.now();
        reportNotification.notificationType = notificationType;
        reportNotification.categoryType = categoryType;
        return reportNotification;
    }

}