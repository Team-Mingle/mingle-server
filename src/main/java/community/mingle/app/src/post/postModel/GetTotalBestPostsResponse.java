package community.mingle.app.src.post.postModel;

import java.time.LocalDateTime;
import java.util.List;

public class GetTotalBestPostsResponse {
    private Long totalPostIdx;
    private String title;
    private String contents;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdTime;
}
