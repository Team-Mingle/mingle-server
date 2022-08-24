package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;

import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.post.model.PatchUpdatePostRequest;
import community.mingle.app.src.post.model.PostCreateRequest;

import community.mingle.app.src.domain.Univ.UnivPostScrap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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


    @OneToMany(mappedBy = "totalPost")
    private List<TotalPostScrap> totalPostScraps = new ArrayList<>();

    @OneToMany(mappedBy = "totalPost")
    private List<TotalPostImage> totalPostImages = new ArrayList<>();

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
    private Boolean isAnonymous;

    @Column(name = "view_count", columnDefinition = "integer default 0", nullable = false)
    private int viewCount;



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
        totalPost.setIsAnonymous(req.getIsAnonymous());
        totalPost.status = PostStatus.ACTIVE;
        return totalPost;
    }

    public void updateTotalPost (PatchUpdatePostRequest req){
        this.setTitle(req.getTitle());
        this.setContent(req.getContent());
        this.updatedAt = LocalDateTime.now();
        this.status = PostStatus.ACTIVE;
    }

    public void deleteTotalPost (){
        this.deletedAt = LocalDateTime.now();
        this.status = PostStatus.INACTIVE;
    }

    public void modifyReportStatus() {
        this.status = PostStatus.REPORTED;
    }

    public void updateView() {
        if (viewCount == 0) {
            this.viewCount = 1;
        } else{
            this.viewCount = viewCount + 1;
        }

    }





    //postImg 추가하기
//    @OneToMany



}
