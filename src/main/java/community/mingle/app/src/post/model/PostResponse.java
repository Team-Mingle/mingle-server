package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostImage;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostImage;
import community.mingle.app.src.domain.UserStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertToDateAndTime;
import static community.mingle.app.src.domain.PostStatus.DELETED;
import static community.mingle.app.src.domain.PostStatus.REPORTED;

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
    private boolean isReported;
    private String createdAt;
    private final int viewCount;
    private List<String> postImgUrl = new ArrayList<>();
    private boolean isAdmin;



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
        this.isReported = false;
        this.isAdmin = totalPost.getMember().getRole().equals("ADMIN");
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
        this.isBlinded = isBlinded;
        createdAt = convertToDateAndTime(u.getCreatedAt());
        this.viewCount =  u.getViewCount();
        if(u.getIsFileAttached()) {
            List<UnivPostImage> univPostImages = u.getUnivPostImages();
            for (UnivPostImage pi : univPostImages) {
                this.postImgUrl.add(pi.getImgUrl());
            }
        }
        this.isReported = false; // 2/17 추가
        this.isAdmin = u.getMember().getRole().equals("ADMIN");
    }




    /**
     * With Report
     */
    public PostResponse(TotalPost totalPost, boolean isMyPost, boolean isLiked, boolean isScraped, boolean isBlinded, String reportedReason) {
        this.postId = totalPost.getId();
//        this.title = totalPost.getTitle();
//        this.content = totalPost.getContent();
        if (totalPost.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = totalPost.getMember().getNickname();
        }
        this.isFileAttached = totalPost.getIsFileAttached();
        this.likeCount = totalPost.getTotalPostLikes().size();
        this.scrapCount = totalPost.getTotalPostScraps().size();
        /** 댓글 개수*/
//        List<TotalComment> commentList = totalPost.getTotalPostComments();
//        List<TotalComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
//        this.commentCount = activeComments.size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isScraped = isScraped;
        this.isBlinded = isBlinded;
        this.createdAt = convertToDateAndTime(totalPost.getCreatedAt());
        this.viewCount = totalPost.getViewCount();
//        if (totalPost.getIsFileAttached()) {
//            List<TotalPostImage> totalPostImages = totalPost.getTotalPostImages();
//            for (TotalPostImage pi : totalPostImages) {
//                this.postImgUrl.add(pi.getImgUrl());
//            }
//        }
        this.isReported = totalPost.getStatus().equals(REPORTED) || totalPost.getStatus().equals(DELETED); // 2/17 추가
        if (totalPost.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.content = "사유: " + reportedReason;
        }
        if (totalPost.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.content = "사유: 이용약관 제 12조 위반";
        }
        this.isAdmin = totalPost.getMember().getRole().equals("ADMIN");
    }

    public PostResponse(UnivPost u, boolean isMyPost, boolean isLiked, boolean isScraped, boolean isBlinded, String reportedReason) {
        postId = u.getId();
//        title = u.getTitle();
//        content = u.getContent();
        if (u.getIsAnonymous()) {
            this.nickname = "익명";
        } else {
            this.nickname = u.getMember().getNickname();
        }
        this.isFileAttached = u.getIsFileAttached();
        likeCount = u.getUnivPostLikes().size();
        scrapCount = u.getUnivPostScraps().size();
        /** 댓글 개수*/
//        List<UnivComment> commentList = u.getUnivComments();
//        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
//        this.commentCount = activeComments.size();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
        this.isScraped = isScraped;
        this.isBlinded = isBlinded;
        createdAt = convertToDateAndTime(u.getCreatedAt());
        this.viewCount =  u.getViewCount();
//        if(u.getIsFileAttached()) {
//            List<UnivPostImage> univPostImages = u.getUnivPostImages();
//            for (UnivPostImage pi : univPostImages) {
//                this.postImgUrl.add(pi.getImgUrl());
//            }
//        }
        this.isReported = u.getStatus().equals(REPORTED) || u.getStatus().equals(DELETED); // 2/17 추가
        if (u.getStatus().equals(REPORTED)) {
            this.title = "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            this.content = "사유: " + reportedReason;
        }
        if (u.getStatus().equals(DELETED)) {
            this.title = "운영규칙 위반에 따라 삭제된 글입니다.";
            this.content = "사유: 이용약관 제 12조 위반";
        }
        this.isAdmin = u.getMember().getRole().equals("ADMIN");
    }
}
