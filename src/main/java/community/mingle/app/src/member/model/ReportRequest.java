package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.TableType;
import lombok.Getter;


@Getter
public class ReportRequest {
    private TableType tableType;
    private Long contentId;
    private int reportTypeId;

//    private String reason;
}
