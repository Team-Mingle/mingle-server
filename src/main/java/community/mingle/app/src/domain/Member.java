package community.mingle.app.src.domain;

import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostLike;
import community.mingle.app.src.domain.Total.TotalPostScrap;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostLike;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id")
    private UnivName univCategory;

    private String nickName;
    private String email; //regex 추가
    private String pwd; //regex 추가


    /** 학교게시판*/
    @OneToMany(mappedBy = "member")
    private List<UnivPost> userUnivPosts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivPostLike> userUnivPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivPostScrap> userUnivPostScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivComment> userUnivComments = new ArrayList<>();

//    private List<UnivCommentLike> univCommentLikes


    /** 전체게시판*/
    @OneToMany(mappedBy = "member")
    private List<TotalPost> userTotalPosts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalPostLike> userTotalPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalPostScrap> userTotalPostScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalComment> userTotalComments = new ArrayList<>();


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Userstatus status;

}
