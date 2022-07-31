package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivCommentLike;
import lombok.Getter;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

@Getter
public class UnivCoCommentDTO {

    private Long commentId;
    private Long parentCommentId;
    private String nickname;
    private String mention; //멘션 추가
    private String content;
    private int likeCount;
    private String createdTime;
    private boolean isLiked;


    public UnivCoCommentDTO(UnivComment c, UnivComment cc, Long memberId) {

//        boolean contains = cc.getUnivCommentLikes()

        this.commentId = cc.getId();
        this.parentCommentId = cc.getParentCommentId();
        this.nickname = cc.getMember().getNickname(); //jwt userIdx 로 멤버 찾음
        this.content = cc.getContent();
        this.createdTime = convertLocaldatetimeToTime(cc.getCreatedAt());
        this.likeCount = cc.getUnivCommentLikes().size();

        for (UnivCommentLike ucl : cc.getUnivCommentLikes()) { //영속성
            if (ucl.getMember().getId() == memberId) { //배치사이즈?
                isLiked = true;
                break;
            } else {
                isLiked = false;
            }
        }
    }

}
