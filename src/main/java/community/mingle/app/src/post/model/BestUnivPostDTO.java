package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
//@AllArgsConstructor
public class BestUnivPostDTO {

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


    public BestUnivPostDTO(UnivPost p, Long memberId) {
        postId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        if (p.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        this.isFileAttached = p.getIsFileAttached();
        if (p.getUnivBlinds().stream().anyMatch(bm -> bm.getMember().getId() == memberId)) {
            this.isBlinded = true;
        }else{
            this.isBlinded = false;
        }
//        this.isBlinded = member.
//        likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        this.likeCount = p.getUnivPostLikes().size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
