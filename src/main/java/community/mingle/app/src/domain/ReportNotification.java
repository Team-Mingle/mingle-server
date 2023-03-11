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
    @Lob
    @Column(name = "board_type", nullable = false)
    private String boardType;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}