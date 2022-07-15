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
@Table(name="univ_name")

public class UnivName {

    @Id
    @Column(name ="univ_id")
    private int id;

    @Column(name = "univ_name")
    private String name;

    /*private List<User> members */
    //단방향?

}
