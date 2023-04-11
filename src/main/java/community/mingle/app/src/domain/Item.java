package community.mingle.app.src.domain;

import community.mingle.app.src.item.model.CreateItemRequest;
import community.mingle.app.src.item.model.ModifyItemPostRequest;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @NotNull
    @Column(columnDefinition = "TEXT",name = "content", nullable = false)
    private String content;

    @Size(max = 100)
    @NotNull
    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @NotNull
    @Column(columnDefinition = "TEXT", name = "chat_url", nullable = false)
    private String chatUrl;

    @NotNull
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", name = "status", nullable = false)
    private ItemStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    @OneToMany(mappedBy = "item")
    private List<ItemLike> itemLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<ItemComment> itemCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<ItemImg> itemImgList = new ArrayList<>();

    public static Item createItemPost(Member member, CreateItemRequest createItemRequest) {
        Item item = new Item();
        item.setTitle(createItemRequest.getTitle());
        item.setPrice(createItemRequest.getPrice());
        item.setContent(createItemRequest.getContent());
        item.setLocation(createItemRequest.getLocation());
        item.setChatUrl(createItemRequest.getChatUrl());
        item.setIsAnonymous(createItemRequest.getIsAnonymous());
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setStatus(ItemStatus.SELLING);
        item.setMember(member);
        return item;
    }

    public void updateView() {
        if (viewCount == 0) {
            this.viewCount = 1;
        } else {
            this.viewCount += 1;
        }
    }

    public void updateItemPost(ModifyItemPostRequest request) {
        this.setTitle(request.getTitle());
        this.setContent(request.getContent());
        this.setChatUrl(request.getChatUrl());
        this.setPrice(request.getPrice());
        this.setLocation(request.getLocation());
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteItemPost() {
        this.deletedAt = LocalDateTime.now();
        this.status = ItemStatus.INACTIVE;
    }

    public void modifyItemStatus(String itemStatus) {
        switch (itemStatus) {
            case "판매중" :
                this.status = ItemStatus.SELLING;
                break;
            case "예약중" :
                this.status = ItemStatus.RESERVED;
                break;
            case "판매완료" :
                this.status = ItemStatus.SOLDOUT;
                break;
        }
    }
}