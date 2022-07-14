package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class UnivName {

    @Id
    @Column(name ="univ_id")
    private String id;

    private String univName;

    /*private List<User> members */
    //단방향?

}
