package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.Item;
import community.mingle.app.src.domain.ItemImg;
import community.mingle.app.src.domain.PostStatus;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class ItemListDTO {

    private final Long id;
    private final String title;
    private final Long price;
    private final String createdAt;
    private final int likeCount;
    private final int commentCount;
    private final String status;
    private final String imgThumbnailUrl;
    private final String content;
    private final String location;
    private final List<String> itemImgList;
    private final String chatUrl;
    private final boolean isLiked;
    private String nickName;

    public ItemListDTO(Item item, Long memberId) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.nickName = item.getMember().getNickname();
        if (item.getIsAnonymous()) {
            this.nickName = "익명";
        } else {
            this.nickName = item.getMember().getNickname();
        }

        this.createdAt = convertLocaldatetimeToTime(item.getCreatedAt());
        this.likeCount = item.getItemLikeList().size();
        this.commentCount = (int) item.getItemCommentList().stream().filter(ic -> ic.getStatus().equals(PostStatus.ACTIVE)).count();
        this.content = item.getContent();
        this.location = item.getLocation();
        this.chatUrl = item.getChatUrl();
        if (item.getItemImgList().isEmpty()) {
            this.itemImgList = null;
        } else {
            this.itemImgList = item.getItemImgList().stream().map(ItemImg::getImgUrl).collect(Collectors.toList());
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
