package community.mingle.app.src.member.model;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
@AllArgsConstructor
public class UnivPostScrapDTO {

    private Long univPostId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private String createdTime;


    public UnivPostScrapDTO(UnivPost p) {
        univPostId = p.getId();
        title = p.getTitle();
        contents = p.getContent();
        nickname = p.getMember().getNickname();
        likeCount = p.getUnivPostLikes().size();
        /** 댓글 개수*/
        List<UnivComment> commentList = p.getUnivComments();
        List<UnivComment> activeComments = commentList.stream().filter(ac -> ac.getStatus().equals(PostStatus.ACTIVE)).collect(Collectors.toList());
        this.commentCount = activeComments.size();
        createdTime = convertLocaldatetimeToTime(p.getCreatedAt());
    }

}
