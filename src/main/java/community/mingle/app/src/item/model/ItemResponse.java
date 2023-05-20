package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class ItemResponse {

    private Long itemId;
    private String title;
    private String content;

    //중고거래 추가
    private Long price;
    private String location;
    private String chatUrl;

    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
//    private int scrapCount; 없음
    private int commentCount;
    private boolean isMyPost;
    private boolean isLiked;
//    private boolean isScraped; 없음
    private boolean isBlinded;
    private boolean isReported;
    private String createdAt;
    private final int viewCount;
    private List<String> postImgUrl = new ArrayList<>();
    private boolean isAdmin;
    private ItemStatus status;


    /**
     * 정상 게시물
     */
    public ItemResponse(Item item, boolean isMyPost, boolean isLiked, boolean isBlinded) {
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.content = item.getContent();

        this.price = item.getPrice();
        this.location = item.getLocation();
        this.chatUrl = item.getChatUrl();

        if (item.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = item.getMember().getNickname();
        }
        this.isFileAttached = true;
        this.likeCount = item.getItemLikeList().size();
        List<ItemComment> commentList = item.getItemCommentList();
        List<ItemComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isBlinded = isBlinded;
        this.createdAt = convertToDateAndTime(item.getCreatedAt());
        this.viewCount = item.getViewCount();
        List<ItemImg> itemImages = item.getItemImgList();
        for (ItemImg img : itemImages) {
            this.postImgUrl.add(img.getImgUrl());
        }
        this.isReported = false;
        this.isAdmin = item.getMember().getRole().equals(UserRole.ADMIN);
        this.status = item.getStatus();
    }

    /**
     * 신고된 게시물 REPORTED / DELETED
     */
    public ItemResponse (Item item, boolean isMyPost, boolean isLiked, boolean isBlinded, String reportedReason) {
        this.itemId = item.getId();
        if (item.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = item.getMember().getNickname();
        }
        this.isFileAttached = true;
        this.likeCount = item.getItemLikeList().size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isBlinded = isBlinded;
        this.createdAt = convertToDateAndTime(item.getCreatedAt());
        this.viewCount = item.getViewCount();
        this.isReported = item.getStatus().equals(ItemStatus.REPORTED) || item.getStatus().equals(ItemStatus.DELETED); // 2/17 추가
        if (item.getStatus().equals(ItemStatus.REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.content = "사유: " + reportedReason;
        }
        if (item.getStatus().equals(ItemStatus.DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.content = "";
        }
        this.isAdmin = item.getMember().getRole().equals(UserRole.ADMIN);
        this.status = item.getStatus();
    }
}
