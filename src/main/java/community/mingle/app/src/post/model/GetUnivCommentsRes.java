package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Univ.UnivComment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;


@Getter
public class GetUnivCommentsRes {

    private Long commentId;
    private Long parentCommentId;
    private String nickName;
    private String content;
    private String createdTime;
    private int likeCount;
    private List<GetCoCommentsRes> coCommentsList;

    public GetUnivCommentsRes(UnivComment c, List<GetCoCommentsRes> cc) {
        commentId = c.getId();
        parentCommentId = c.getParentCommentId();
        nickName = c.getMember().getNickname();
        content = c.getContent();
        createdTime = convertLocaldatetimeToTime(c.getCreatedAt());
        likeCount = c.getUnivCommentLikes().size();
        coCommentsList = cc;
//        coCommentsList = cc.stream()
//                .map(coComment -> new GetCoCommentsRes(coComment))
//                .collect(Collectors.toList());

//        commentList = u.getComments().stream()
//                .map(comment -> new GetUnivCommentsRes(comment))
//                .collect(Collectors.toList());
//        coCommentList = c.getParentCommentId()
//                .map(comment -> new GetUnivCommentsRes(comment))
//                .collect(Collectors.toList());

    }

}
