package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="policy")

public class Policy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="policy_id")
    private Long id;

    @Column(columnDefinition = "TEXT") //found [text (Types#LONGVARCHAR)], but expecting [varchar(255) (Types#VARCHAR)]
    private String content;


}
