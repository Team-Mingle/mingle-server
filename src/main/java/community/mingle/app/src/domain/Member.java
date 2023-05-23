package community.mingle.app.src.domain;

import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.utils.converter.UserRoleConverter;
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    @Convert(converter = UserRoleConverter.class)
    private UserRole role;

    @Column(name = "fcm_token")
    private String fcmToken;


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
    private List<TotalPost> totalPosts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalPostLike> totalPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalPostScrap> totalPostScraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TotalComment> totalComments = new ArrayList<>();

    /**알림 */
    @OneToMany(mappedBy = "member")
    private List<TotalNotification> totalNotifications= new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivNotification> univNotifications= new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ItemNotification> itemNotifications= new ArrayList<>();
    @OneToMany(mappedBy = "member")
    private List<TotalBlind> totalBlindPost = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<UnivBlind> univBlindPost = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ItemBlind> itemBlind = new ArrayList<>();

    @OneToMany(mappedBy = "blockedMember")
    private List<BlockMember> blockedMember= new ArrayList<>();

    @OneToMany(mappedBy = "blockerMember")
    private List<BlockMember> blockerMember= new ArrayList<>();


    @OneToMany(mappedBy = "member")
    private List<ItemComment> itemCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Item> itemList = new ArrayList<>();


    private LocalDateTime agreedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;


    @Enumerated(EnumType.STRING)
//    @Column(name = "status",columnDefinition = "ENUM('ACTIVE','INACTIVE','REPORTED','ADMIN", nullable = false)
    @Column(columnDefinition = "enum")
    private UserStatus status;


    //== 생성 메서드 ==// -> constructor 역할.
    public static Member createMember(UnivName univName, String nickname, String email, String pwd) {
        Member member = new Member();
        member.setUniv(univName);
        member.setNickname(nickname);
        member.setEmail(email);
        member.setPwd(pwd);
        member.agreedAt = LocalDateTime.now();
        member.createdAt = LocalDateTime.now();
        member.updatedAt = LocalDateTime.now();
        member.status = UserStatus.ACTIVE;
        member.role = UserRole.USER;

        return member;
    }

    public static Member createFreshman(UnivName univName, String nickname, String email, String pwd) {
        Member member = new Member();
        member.setUniv(univName);
        member.setNickname(nickname);
        member.setEmail(email);
        member.setPwd(pwd);
        member.agreedAt = LocalDateTime.now();
        member.createdAt = LocalDateTime.now();
        member.updatedAt = LocalDateTime.now();
        member.status = UserStatus.ACTIVE;
        member.role = UserRole.FRESHMAN;

        return member;
    }


    public void deleteMember() {
        this.deletedAt = LocalDateTime.now();
        this.status = UserStatus.INACTIVE;

    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    //== 비즈니스 로직 ==//
    //닉네임 수정
    public void modifyNickname(String nickname) {
        this.nickname = nickname;
    }

    public void modifyStatusAsReported() {
        this.status = UserStatus.REPORTED;
    }

    public void deleteTotalNotification(List<TotalNotification> totalNotifications){
        this.totalNotifications = totalNotifications;
    }

    public void deleteUnivNotification(List<UnivNotification> univNotifications){
        this.univNotifications = univNotifications;
    }



}
