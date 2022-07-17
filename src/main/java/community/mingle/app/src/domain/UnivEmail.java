package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="univ_email")

public class UnivEmail {

    @Id
    @Column(name ="univ_email_id")
    private int emailIdx;

    @Column(name ="univ_id")
    private int univIdx;

    @Column(name = "univ_domain")
    private String domain;



}
