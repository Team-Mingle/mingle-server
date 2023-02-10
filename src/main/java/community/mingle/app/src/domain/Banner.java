package community.mingle.app.src.domain;

import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.home.model.CreateBannerRequest;
import community.mingle.app.src.post.model.CreatePostRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="banner")
public class Banner {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String url;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String link;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private PostStatus status;

     */

    public static Banner createBanner (Member member, CreateBannerRequest req, String fileName, String link){
        Banner banner = new Banner();

        //후에 req에서 받아서 쓰는 코드 추가될까봐 req 받아둠.
        banner.setMember(member);
        banner.setUrl(fileName);
        banner.setLink(link);
        banner.createdAt = LocalDateTime.now();
        banner.updatedAt = LocalDateTime.now();

        return banner;
    }
}
