package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostImage;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostImage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;

@Getter
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private boolean isFileAttached;
    private int likeCount;
    private int scrapCount;
    private int commentCount;
    private boolean isMyPost;
    private boolean isLiked;
    private boolean isScraped;
    private boolean isBlinded;
    private String createdAt;
    private final int viewCount;
    private List<String> postImgUrl = new ArrayList<>();


    public PostResponse(TotalPost totalPost, boolean isMyPost, boolean isLiked, boolean isScraped, boolean isBlinded) {
        this.postId = totalPost.getId();
        this.title = totalPost.getTitle();
        this.content = totalPost.getContent();
        if (totalPost.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }
        this.isFileAttached = totalPost.getIsFileAttached();
        this.likeCount = totalPost.getTotalPostLikes().size();
        this.scrapCount = totalPost.getTotalPostScraps().size();
        /** 댓글 개수*/
        List<TotalComment> commentList = totalPost.getTotalPostComments();
        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isScraped = isScraped;
        this.isBlinded = isBlinded;
        this.createdAt = convertToDateAndTime(totalPost.getCreatedAt());
        this.viewCount = totalPost.getViewCount();
        if (totalPost.getIsFileAttached() == true) {
            List<TotalPostImage> totalPostImages = totalPost.getTotalPostImages();
            for (TotalPostImage pi : totalPostImages) {
                this.postImgUrl.add(pi.getImgUrl());
            }
        }
    }

    public PostResponse(UnivPost u, boolean isMyPost, boolean isLiked, boolean isScraped, boolean isBlinded) {
        postId = u.getId();
        title = u.getTitle();
        content = u.getContent();
        if (u.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = u.getMember().getNickname();
        }
        this.isFileAttached = u.getIsFileAttached();
        likeCount = u.getUnivPostLikes().size();
        scrapCount = u.getUnivPostScraps().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = u.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isScraped = isScraped;
        createdAt = convertToDateAndTime(u.getCreatedAt());
        this.viewCount =  u.getViewCount();

        if(u.getIsFileAttached() == true) {
            List<UnivPostImage> univPostImages = u.getUnivPostImages();
            for (UnivPostImage pi : univPostImages) {
                this.postImgUrl.add(pi.getImgUrl());
            }
        }
        this.isBlinded = isBlinded;
    }
}
