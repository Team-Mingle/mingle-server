package community.mingle.app.src.domain.Univ;

import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostImage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "univ_post_img")
public class UnivPostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univpost_img_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univpost_id")
    private UnivPost univPost;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static UnivPostImage createTotalPost (UnivPost univPost, String fileName){
        UnivPostImage univPostImage = new UnivPostImage();
        univPostImage.setUnivPost(univPost);
        univPostImage.setImgUrl(fileName);
        univPostImage.createdAt = LocalDateTime.now();
        return univPostImage;
    }
}
