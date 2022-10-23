package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class BestTotalPostListResponse {
    private String boardName;
    private List<BestTotalPostDTO> postListDTO;

    public BestTotalPostListResponse(String boardName, List<BestTotalPostDTO> postListDTO) {
        this.boardName = null;
        this.postListDTO = postListDTO;
    }
}
