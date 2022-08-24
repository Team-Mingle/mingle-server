package community.mingle.app.src.domain.Total;

import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.post.model.PostCreateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "total_post_img")
public class TotalPostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalpost_img_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "totalpost_id")
    private TotalPost totalPost;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static TotalPostImage createTotalPost (TotalPost totalPost, String fileName){
        TotalPostImage totalPostImage = new TotalPostImage();
        totalPostImage.setTotalPost(totalPost);
        totalPostImage.setImgUrl(fileName);
        totalPostImage.createdAt = LocalDateTime.now();
        return totalPostImage;
    }
}
