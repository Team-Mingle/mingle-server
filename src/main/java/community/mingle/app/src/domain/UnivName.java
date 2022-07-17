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
@Table(name="univ_name")

public class UnivName {

    @Id
    @Column(name ="univ_id")
    private int id;

    @Column(name = "univ_name")
    private String name;

    @OneToMany(mappedBy = "univName")
    private List<UnivEmail> univEmailList = new ArrayList<>();

    /*private List<User> members */
    //단방향?

}
