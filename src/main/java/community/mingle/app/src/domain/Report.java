package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="report")

public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="report_id")
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", name = "table_id")
//    @Column(name = "table_id")
    private TableType tableId;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "reported_member_id")
    private Long reportedMemberId;

    @Column(name = "reporter_member_id")
    private Long reporterMemberId;

//    @Column(name = "report_type")
//    private int type;

//    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private ReportStatus status;

    public static Report createReport (TableType tableId, Long contentId, Long reportedMemberId, Long reporterMemberId) {
        Report report = new Report();
        report.setTableId(tableId);
        report.setContentId(contentId);
        report.setReportedMemberId(reportedMemberId);
        report.setReporterMemberId(reporterMemberId);
//        report.setType(type);
//        report.setReason(reason);
        report.createdAt = LocalDateTime.now();
        report.status = ReportStatus.ACTIVE;

        return report;
    }
}
