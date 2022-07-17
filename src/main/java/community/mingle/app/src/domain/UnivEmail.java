package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="univ_email")

public class UnivEmail {

    @Id
    @Column(name ="univ_email_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id")
    private UnivName univName;

    @Column(name = "univ_domain")
    private String domain;

    /*private List<User> members */
    //단방향?

}