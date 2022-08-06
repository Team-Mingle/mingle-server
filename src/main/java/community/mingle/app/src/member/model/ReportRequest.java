package community.mingle.app.src.member.model;

import lombok.Getter;

@Getter
public class ReportRequest {
    private int table_id;
    private Long content_id;
    private int type;
    private String reason;
}
