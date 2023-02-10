package community.mingle.app.src.post.model;


import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class UnivPostListDTO {

    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private boolean isBlinded;
    private int likeCount;
    private int commentCount;
    private String createdAt;
//    private String postImgUrl;


    public UnivPostListDTO(UnivPost univPost, Long memberId) {
        this.postId = univPost.getId();
        this.title = univPost.getTitle();
        this.contents = univPost.getContent();
        if (univPost.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = univPost.getMember().getNickname();
        }
        this.isFileAttached = univPost.getIsFileAttached();
        this.likeCount = univPost.getUnivPostLikes().size();
        if (univPost.getUnivBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId))) {
            this.isBlinded = true;
        } else{
            this.isBlinded = false;
        }

        /** 댓글 개수*/
        List<UnivComment> commentList = univPost.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();

        this.createdAt = convertLocaldatetimeToTime(univPost.getCreatedAt());
//        if(univPost.getIsFileAttached() == true) {
//            this.postImgUrl = univPost.getUnivPostImages().get(0).getImgUrl();
//        }
    }

}

