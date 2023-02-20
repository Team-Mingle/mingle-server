package community.mingle.app.src.home.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
//@AllArgsConstructor
public class HomeBestUnivPostResponse {

    private Long postId;
    private String title;
    private String contents;
    private String nickname;
    private boolean isFileAttached;
    private boolean isBlinded;
    private int likeCount;
    private int commentCount;
    private String createdAt;


    public HomeBestUnivPostResponse(UnivPost p, Long memberId) {
        postId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        if (p.getIsAnonymous() == true) {
            this.nickname = "익명";
        } else {
            this.nickname = p.getMember().getNickname();
        }
        if (p.getUnivBlinds().stream().anyMatch(bm -> Objects.equals(bm.getMember().getId(), memberId))) {
            this.isBlinded = true;
        }else{
            this.isBlinded = false;
        }
        isFileAttached = p.getIsFileAttached();
        likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        createdAt = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
