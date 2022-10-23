package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.*;

import community.mingle.app.src.post.model.UpdatePostRequest;

import community.mingle.app.src.post.model.CreatePostRequest;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="univ_post")

public class UnivPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univpost_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "univPost")
    private List<UnivComment> univComments = new ArrayList<>();


    /** 3.3 추가 */
    @OneToMany(mappedBy = "univPost")
    private List<UnivPostLike> univPostLikes = new ArrayList<>();


    /**
     * 3.10 추가
     */
    @OneToMany(mappedBy = "univPost")
    private List<UnivPostScrap> univPostScraps = new ArrayList<>();

    @OneToMany(mappedBy = "univPost")
    private List<UnivPostImage> univPostImages = new ArrayList<>();

    /** 3.3 추가  - 단방향 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id")
    private UnivName univName;


    //    @Enumerated(EnumType.STRING)
//    private PostCategory category; //enum
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;//글자수제한
    private String content; //글자수제한

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

//    @JsonProperty("isAnonymous")
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(name = "is_fileattached")
    private Boolean isFileAttached;

    @Column(name = "view_count", columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private PostStatus status;


    public static UnivPost createUnivPost (Member member, Category category, CreatePostRequest req) {
        UnivPost univPost = new UnivPost();
        univPost.setMember(member);
        univPost.setUnivName(member.getUniv());
        univPost.setCategory(category);
        univPost.setTitle(req.getTitle());
        univPost.setContent(req.getContent());
        univPost.createdAt = LocalDateTime.now();
        univPost.updatedAt = LocalDateTime.now();
        univPost.setIsAnonymous(req.getIsAnonymous());
//        univPost.setIsFileAttached(req.getIsFileAttached());

//        System.out.println(req.getMultipartFile().isEmpty()); //포스트맨으로 실행 시, false
//        System.out.println(req.getMultipartFile().get(0)); // 뭐가있다
//        System.out.println(req.getMultipartFile() == null); //널도 아니다 false

//        if (req.getMultipartFile()==null || req.getMultipartFile().isEmpty()) {
//            univPost.setIsFileAttached(false);
//        } else {
//            univPost.setIsFileAttached(true);
//        }

//      //Better logic below
        if (req.getMultipartFile() != null && !(req.getMultipartFile().isEmpty())) {
            univPost.setIsFileAttached(true);
        }
        else {
            univPost.setIsFileAttached(false);
        }

        univPost.status = PostStatus.ACTIVE;
        return univPost;
    }

    public void updateUnivPost (UpdatePostRequest req){
        this.setTitle(req.getTitle());
        this.setContent(req.getContent());
        this.updatedAt = LocalDateTime.now();
        this.status = PostStatus.ACTIVE;
    }

    public void modifyReportStatus() {
        this.status = PostStatus.REPORTED;
    }

    //== 비즈니스 로직 == //
//    public static boolean isLiked(Member member) {
//        if (this.univPostLikes)
//    }


    public void deleteUnivPost (){
        this.deletedAt = LocalDateTime.now();
        this.status = PostStatus.INACTIVE;
    }


    public void updateView() {
        if (viewCount == 0) {
            this.viewCount = 1;
        } else{
            this.viewCount = viewCount + 1;
        }

    }
}
