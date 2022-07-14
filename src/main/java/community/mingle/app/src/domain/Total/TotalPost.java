package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostCategory;
import community.mingle.app.src.domain.PostCommentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class TotalPost {

    @Id @GeneratedValue
    @Column(name = "totalpost_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "totalPost")
    private List<TotalComment> postTotalcomments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PostCategory postCategory; //enum


    private String title;//글자수제한
    private String content; //글자수제한


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


    private boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    private PostCommentStatus postCommentStatus;


}
