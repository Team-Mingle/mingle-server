package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Total.TotalComment;

import java.util.List;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;

public class TotalCommentDto {

    private Long commentId;
    private String content;
    private int likeCount;
    private String nickname;
    private String createdAt;
    private List<TotalCocommentDto> totalCocommentDtoList;

    public TotalCommentDto(TotalComment totalComment, List<TotalCocommentDto> totalCocommentDtoList) {
        this.commentId = totalComment.getId();
        this.content = totalComment.getContent();
        this.likeCount = totalComment.getTotalCommentLikes().size();
        if (totalComment.isAnonymous() == true) {
            this.nickname = "익명 "+totalComment.getAnonymousId();
        } else {
            this.nickname = totalComment.getMember().getNickname();
        }
        this.createdAt = convertLocaldatetimeToTime(totalComment.getCreatedAt());
        this.totalCocommentDtoList = totalCocommentDtoList;
    }

}
