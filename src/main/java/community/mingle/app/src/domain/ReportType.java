package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="report_type")
public class ReportType {


    @Id
    @Column(name ="type_id")
    private int id;

    @Column(name = "type")
    private String type;


}
