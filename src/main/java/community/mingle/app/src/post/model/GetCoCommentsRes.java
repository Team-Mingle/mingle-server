package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivComment;
import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

import lombok.Getter;

@Getter
public class GetCoCommentsRes {

    private Long commentId;
    private Long parentCommentId;
    private String nickName;
    private String content;
    private String createdTime;
    private int likeCount;

    public GetCoCommentsRes(UnivComment cc) {
        this.commentId = cc.getId();
        this.parentCommentId = cc.getParentCommentId();
        this.nickName = cc.getMember().getNickname(); //jwt userIdx 로 멤버 찾음
        this.content = cc.getContent();
        this.createdTime = convertLocaldatetimeToTime(cc.getCreatedAt());
        this.likeCount = cc.getUnivCommentLikes().size();
    }
}
