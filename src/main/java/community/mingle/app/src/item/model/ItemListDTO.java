package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.Item;
import community.mingle.app.src.domain.ItemImg;
import community.mingle.app.src.domain.ItemLike;
import community.mingle.app.src.domain.Total.TotalCommentLike;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private String content;
    private String location;
    private List<String> itemImgList;

    private boolean isLiked;

    public ItemListDTO(Item item, Long memberId) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.nickName = item.getMember().getNickname();
        this.createdAt = convertLocaldatetimeToTime(item.getCreatedAt());
        this.likeCount = item.getItemLikeList().size();
        this.commentCount = item.getItemCommentList().size();
        this.content = item.getContent();
        this.location = item.getLocation();
        if (item.getItemImgList().isEmpty()) {
            this.itemImgList = null;
        } else {
            this.itemImgList = item.getItemImgList().stream().map(itemImg -> itemImg.getImgUrl()).collect(Collectors.toList());
        }
        this.status = item.getStatus().getName();
        if (item.getItemImgList().isEmpty()) {
            this.imgThumbnailUrl = null;
        } else {
            this.imgThumbnailUrl = item.getItemImgList().get(0).getImgUrl();
        }
        this.isLiked = item.getItemLikeList().stream()
                .anyMatch(itemLike -> Objects.equals(itemLike.getMember().getId(), memberId));
    }
}
