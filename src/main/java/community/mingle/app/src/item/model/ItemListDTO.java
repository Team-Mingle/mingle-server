package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.Item;
import lombok.Getter;
import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class ItemListDTO {

    private Long id;
    private String title;
    private Long price;
    private String nickName;
    private String createdAt;
    private int likeCount;
    private int commentCount;
    private String status;
    private String imgThumbnailUrl;

    public ItemListDTO(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.nickName = item.getMember().getNickname();
        this.createdAt = convertLocaldatetimeToTime(item.getCreatedAt());
        this.likeCount = item.getItemLikeList().size();
        this.commentCount = item.getItemCommentList().size();
        this.status = item.getStatus().getName();
        if (item.getItemImgList().isEmpty()) {
            this.imgThumbnailUrl = null;
        } else {
            this.imgThumbnailUrl = item.getItemImgList().get(0).getImgUrl();
        }
    }
}
