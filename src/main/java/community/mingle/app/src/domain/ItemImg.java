package community.mingle.app.src.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "item_img")
public class ItemImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id", nullable = false)
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT",name = "img_url", nullable = false)
    private String imgUrl;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static ItemImg createItemImg(Item item, String fileName) {
        ItemImg itemImg = new ItemImg();
        itemImg.setItem(item);
        itemImg.setImgUrl(fileName);
        itemImg.setCreatedAt(LocalDateTime.now());
        itemImg.setUpdatedAt(LocalDateTime.now());
        return itemImg;
    }
}