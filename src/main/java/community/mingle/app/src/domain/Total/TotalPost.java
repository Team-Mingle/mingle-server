package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.model.PatchUpdatePostRequest;
import community.mingle.app.src.post.model.PostCreateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "total_post")
public class TotalPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalpost_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "totalPost")
    private List<TotalComment> totalPostComments = new ArrayList<>();

    @OneToMany(mappedBy = "totalPost")
    private List<TotalPostLike> totalPostLikes = new ArrayList<>();


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

    public static TotalPost createTotalPost (Member member, Category category, PostCreateRequest req){
        TotalPost totalPost = new TotalPost();
        totalPost.setMember(member);
        totalPost.setCategory(category);
        totalPost.setTitle(req.getTitle());
        totalPost.setContent(req.getContent());
        totalPost.createdAt = LocalDateTime.now();
        totalPost.updatedAt = LocalDateTime.now();
        totalPost.setAnonymous(req.isAnonymous());
        totalPost.status = PostStatus.ACTIVE;
        return totalPost;
    }

    public void updateTotalPost (PatchUpdatePostRequest req){
        this.setTitle(req.getTitle());
        this.setContent(req.getContent());
        this.updatedAt = LocalDateTime.now();
        this.status = PostStatus.ACTIVE;
    }

}
