package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.app.config.DateTimeConverter.convertLocaldatetimeToTime;
import static java.util.stream.Collectors.toList;

@Getter
@AllArgsConstructor
public class GetUnivPostDetailResponse {

    private Long univPostId;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private int scrapCount;
    private String createdTime;
    private List<GetUnivCommentsRes> commentList;

//    private boolean isMyPost;     // 내가 쓴 글인지 확인 (Member)

//    private List<GetPostImgRes> imgs;

//   조회수?

    public GetUnivPostDetailResponse(UnivPost u, List<GetUnivCommentsRes> fullComment) {
        univPostId = u.getId();
        title = u.getTitle();
        contents = u.getContent();
        nickname = u.getMember().getNickname();
        likeCount = u.getUnivPostLikes().size();
        commentCount = u.getComments().size();
        scrapCount = u.getUnivPostScraps().size();
        createdTime = convertLocaldatetimeToTime(u.getCreatedAt());
        commentList = fullComment;
//        commentList = u.getComments().stream()
//                .map(comment -> new GetUnivCommentsRes(comment,coComments))
//                .collect(Collectors.toList());
    }






}
