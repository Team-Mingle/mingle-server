package community.mingle.app.src.domain;

import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostLike;
import community.mingle.app.src.domain.Total.TotalPostScrap;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostLike;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import lombok.*;
/** Setter 주의 */


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="member")

public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id")
    private UnivName univ;

    private String nickname;
    private String email; //regex 추가
    private String pwd; //regex 추가


    /** 학교게시판*/
    @OneToMany(mappedBy = "member")
    private List<UnivPost> univPosts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivPostLike> univPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivPostScrap> univPostScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivComment> univComments = new ArrayList<>();

//    private List<UnivCommentLike> univCommentLikes


    /** 전체게시판*/
    @OneToMany(mappedBy = "member")
    private List<TotalPost> total_posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalPostLike> total_post_likes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalPostScrap> total_post_scraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalComment> total_comments = new ArrayList<>();


    private LocalDateTime agreed_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    @Enumerated(EnumType.STRING)
//    @Column(name = "status",columnDefinition = "ENUM('ACTIVE','INACTIVE','REPORTED','ADMIN", nullable = false)
    @Column(columnDefinition = "enum")
    private Userstatus status;


    //== 생성 메서드 ==// -> constructor 역할.
    public static Member createMember(UnivName univName, String nickname, String email, String pwd) {
        Member member = new Member();
        member.setUniv(univName);
        member.setNickname(nickname);
        member.setEmail(email);
        member.setPwd(pwd);
        member.agreed_at = LocalDateTime.now();
        member.created_at = LocalDateTime.now();
        member.updated_at = LocalDateTime.now();
        member.status = Userstatus.ACTIVE;

        return member;
    }


    //== 비즈니스 로직 ==//
    //닉네임 수정
    public void modifyNickname(String nickname) {
        this.nickname = nickname;
    }


}
