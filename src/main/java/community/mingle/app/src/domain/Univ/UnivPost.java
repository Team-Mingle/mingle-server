package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.*;
import community.mingle.app.src.post.model.PatchUpdatePostRequest;
import community.mingle.app.src.post.model.PostCreateRequest;
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
    private List<UnivComment> comments = new ArrayList<>();


    /** 2.3 추가 */
    @OneToMany(mappedBy = "univPost")
    private List<UnivPostLike> univPostLikes = new ArrayList<>();

    /** 2.3 추가  - 단방향 */
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

    @Column(name = "is_anonymous")
    private boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private PostStatus status;


    public static UnivPost createUnivPost (Member member, Category category, PostCreateRequest req){
        UnivPost univPost = new UnivPost();
        univPost.setMember(member);
        univPost.setUnivName(member.getUniv());
        univPost.setCategory(category);
        univPost.setTitle(req.getTitle());
        univPost.setContent(req.getContent());
        univPost.createdAt = LocalDateTime.now();
        univPost.updatedAt = LocalDateTime.now();
        univPost.setAnonymous(req.isAnonymous());
        univPost.status = PostStatus.ACTIVE;
        return univPost;
    }

    public void updateUnivPost (PatchUpdatePostRequest req){
        this.setTitle(req.getTitle());
        this.setContent(req.getContent());
        this.updatedAt = LocalDateTime.now();
        this.status = PostStatus.ACTIVE;
    }

    public void deleteUnivPost (){
        this.deletedAt = LocalDateTime.now();
        this.status = PostStatus.INACTIVE;
    }
}
