package community.mingle.app.src.domain;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Column(name = "country", nullable = false)
    private String countryName;

    @OneToMany(mappedBy = "country")
    private List<UnivName> univNameList = new ArrayList<>();

}